package com.anima.features.auth.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anima.features.auth.entities.RefreshTokenEntity
import com.anima.features.auth.repositories.RefreshTokenRepository
import com.anima.features.user.entities.UserEntity
import com.anima.features.user.models.AccountType
import com.anima.features.user.repositories.UserRepository
import com.anima.features.visitor.repositories.VisitorRepository
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.features.exhibitor.entities.ExhibitorEntity
import com.anima.features.exhibitor.repositories.ExhibitorRepository
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.auth.exceptions.EmailAlreadyExistsException
import com.anima.features.auth.dtos.TokenResponseDto
import com.anima.features.auth.exceptions.InvalidCredentialsException
import com.anima.features.auth.exceptions.InvalidRefreshTokenException
import com.anima.features.auth.exceptions.TooManyAttemptsException
import com.anima.features.auth.ratelimit.RateLimiter
import com.anima.utils.TokenHashUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64
import java.util.Locale

private const val MAX_EMAIL_LENGTH = 254
private const val MAX_PASSWORD_LENGTH = 128
private const val MAX_REFRESH_TOKEN_LENGTH = 256
private const val MAX_ACTIVE_REFRESH_TOKENS = 10
private const val BCRYPT_COST = 12

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val visitorRepository: VisitorRepository,
    private val exhibitorRepository: ExhibitorRepository,
    @Value($$"${jwt.refresh-token-expiration-days}") private val refreshTokenExpirationDays: Long
) {
    private val secureRandom = SecureRandom()

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }

    // 5 failed logins per email in 15 minutes locks that email for the rest of the window
    private val loginFailures = RateLimiter(maxAttempts = 5, windowMillis = 15 * 60_000)

    // verified against when the email is unknown, so both paths spend the same bcrypt time
    private val dummyHash = BCrypt.withDefaults().hashToString(BCRYPT_COST, "not-a-real-password".toCharArray())

    @Transactional
    fun login(email: String, rawPassword: String): TokenResponseDto {
        if (email.length > MAX_EMAIL_LENGTH || rawPassword.length > MAX_PASSWORD_LENGTH) throw InvalidCredentialsException()

        val key = email.trim().lowercase(Locale.ROOT)
        if (loginFailures.isBlocked(key)) throw TooManyAttemptsException()

        val user = userRepository.findByEmail(key).orElse(null)
        val passwordOk = BCrypt.verifyer()
            .verify(rawPassword.toCharArray(), user?.passwordHash ?: dummyHash)
            .verified

        if (user == null || !passwordOk) {
            loginFailures.hit(key)
            throw InvalidCredentialsException()
        }
        loginFailures.reset(key)

        val accessToken = jwtService.generateAccessToken(user.id!!, user.email, accountTypeOf(user))
        val refreshToken = issueRefreshToken(user)

        return TokenResponseDto(accessToken, refreshToken)
    }

    @Transactional
    fun register(request: RegisterRequestDto): TokenResponseDto {
        val name = request.name.trim()
        val email = request.email.trim().lowercase()
        require(name.isNotBlank()) { "Name is required" }
        require(EMAIL_REGEX.matches(email)) { "Invalid email" }
        require(request.password.length >= MIN_PASSWORD_LENGTH) { "Password must have at least $MIN_PASSWORD_LENGTH characters" }
        if (userRepository.existsByEmail(email)) throw EmailAlreadyExistsException()

        val user = userRepository.save(UserEntity().apply {
            this.name = name
            this.email = email
            passwordHash = BCrypt.withDefaults().hashToString(BCRYPT_COST, request.password.toCharArray())
        })
        when (request.accountType) {
            AccountType.VISITOR -> visitorRepository.save(VisitorEntity().apply { this.user = user })
            AccountType.EXHIBITOR -> exhibitorRepository.save(ExhibitorEntity().apply { this.user = user })
        }

        return TokenResponseDto(
            jwtService.generateAccessToken(user.id!!, user.email, request.accountType),
            issueRefreshToken(user),
        )
    }

    // a failed refresh must not roll back the family revocation done on token reuse
    @Transactional(noRollbackFor = [InvalidRefreshTokenException::class])
    fun refresh(rawRefreshToken: String): TokenResponseDto {
        if (rawRefreshToken.length > MAX_REFRESH_TOKEN_LENGTH) throw InvalidRefreshTokenException()

        val tokenHash = TokenHashUtil.sha256(rawRefreshToken)
        val stored = refreshTokenRepository.findByTokenHash(tokenHash) ?: throw InvalidRefreshTokenException()
        val now = Instant.now()
        // read while still attached, the revoke below clears the persistence context
        val user = stored.user
        val userId = user.id!!
        val email = user.email

        // token rotation: exactly one caller can flip the token from active to revoked
        if (!refreshTokenRepository.revokeIfActive(tokenHash, now)) {
            // a known token that was already used (or lost the race) is a replay, assume it leaked
            if (refreshTokenRepository.findByTokenHash(tokenHash)?.revoked == true) {
                refreshTokenRepository.deleteAllByUserId(userId)
            }
            throw InvalidRefreshTokenException()
        }

        val accessToken = jwtService.generateAccessToken(userId, email, accountTypeOf(user))
        val newRefreshToken = issueRefreshToken(user)

        return TokenResponseDto(accessToken, newRefreshToken)
    }

    fun logout(rawRefreshToken: String) {
        if (rawRefreshToken.length > MAX_REFRESH_TOKEN_LENGTH) return
        val tokenHash = TokenHashUtil.sha256(rawRefreshToken)
        refreshTokenRepository.revokeByTokenHash(tokenHash)
    }

    // a user with a visitor row is a visitor, otherwise an exhibitor
    private fun accountTypeOf(user: UserEntity) =
        if (visitorRepository.existsByUserId(user.id!!)) AccountType.VISITOR else AccountType.EXHIBITOR

    private fun issueRefreshToken(user: UserEntity): String {
        val rawToken = generateOpaqueToken()
        val now = Instant.now()
        refreshTokenRepository.save(
            RefreshTokenEntity(
                tokenHash = TokenHashUtil.sha256(rawToken),
                user = user,
                expiresAt = now.plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60)
            )
        )
        // oldest sessions are dropped once a user goes over the cap
        val active = refreshTokenRepository.findActiveByUserId(user.id!!, now)
        active.take((active.size - MAX_ACTIVE_REFRESH_TOKENS).coerceAtLeast(0))
            .forEach { refreshTokenRepository.deleteById(it.id!!) }
        return rawToken
    }

    private fun generateOpaqueToken(): String {
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
