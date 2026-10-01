package com.anima.features.auth.services

import com.anima.features.user.models.AccountType
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import java.util.Optional
import java.util.UUID

@Service
class JwtService(
    @Value($$"${jwt.secret}") private val secret: String,
    @Value($$"${jwt.issuer}") private val issuer: String,
    @Value($$"${jwt.access-token-expiration-minutes}") private val accessTokenExpirationMinutes: Long
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(userId: UUID, email: String, accountType: AccountType): String {
        val now = Instant.now()
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("accountType", accountType.name)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(accessTokenExpirationMinutes * 60)))
            .sign(algorithm)
    }

    fun validateAndDecode(token: String): Optional<DecodedJWT> =
        try {
            Optional.of(
                JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
            )
        } catch (e: JWTVerificationException) {
            Optional.empty()
        }
}