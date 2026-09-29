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
import com.anima.utils.TokenHashUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64

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

    @Transactional
    fun login(email: String, rawPassword: String): TokenResponseDto {
        val userOpt = userRepository.findByEmail(email)
        if (userOpt.isEmpty) throw InvalidCredentialsException()

        val user = userOpt.get()

        val passwordOk = BCrypt.verifyer()
            .verify(rawPassword.toCharArray(), user.passwordHash)
            .verified

        if (!passwordOk) throw InvalidCredentialsException()

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
            passwordHash = BCrypt.withDefaults().hashToString(10, request.password.toCharArray())
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

    @Transactional
    fun refresh(rawRefreshToken: String): TokenResponseDto {
        val tokenHash = TokenHashUtil.sha256(rawRefreshToken)
        val stored = refreshTokenRepository.findByTokenHash(tokenHash)
            ?.takeIf { !it.revoked && it.expiresAt.isAfter(Instant.now()) }
            ?: throw InvalidRefreshTokenException()

        // token rotation
        refreshTokenRepository.revokeByTokenHash(tokenHash)

        val accessToken = jwtService.generateAccessToken(stored.user.id!!, stored.user.email, accountTypeOf(stored.user))
        val newRefreshToken = issueRefreshToken(stored.user)

        return TokenResponseDto(accessToken, newRefreshToken)
    }

    fun logout(rawRefreshToken: String) {
        val tokenHash = TokenHashUtil.sha256(rawRefreshToken)
        refreshTokenRepository.revokeByTokenHash(tokenHash)
    }

    // a user with a visitor row is a visitor, otherwise an exhibitor
    private fun accountTypeOf(user: UserEntity) =
        if (visitorRepository.existsByUserId(user.id!!)) AccountType.VISITOR else AccountType.EXHIBITOR

    private fun issueRefreshToken(user: UserEntity): String {
        val rawToken = generateOpaqueToken()
        refreshTokenRepository.save(
            RefreshTokenEntity(
                tokenHash = TokenHashUtil.sha256(rawToken),
                user = user,
                expiresAt = Instant.now().plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60)
            )
        )
        return rawToken
    }

    private fun generateOpaqueToken(): String {
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}