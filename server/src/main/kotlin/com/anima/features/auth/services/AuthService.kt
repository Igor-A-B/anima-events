package com.anima.features.auth.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anima.features.auth.entities.RefreshTokenEntity
import com.anima.features.auth.repositories.RefreshTokenRepository
import com.anima.features.user.entities.UserEntity
import com.anima.features.user.models.AccountType
import com.anima.features.user.repositories.UserRepository
import com.anima.features.visitor.repositories.VisitorRepository
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
    @Value($$"${jwt.refresh-token-expiration-days}") private val refreshTokenExpirationDays: Long
) {
    private val secureRandom = SecureRandom()

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