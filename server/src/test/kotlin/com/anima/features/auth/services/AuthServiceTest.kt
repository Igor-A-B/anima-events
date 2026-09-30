package com.anima.features.auth.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anima.features.auth.entities.RefreshTokenEntity
import com.anima.features.auth.exceptions.InvalidCredentialsException
import com.anima.features.auth.exceptions.InvalidRefreshTokenException
import com.anima.features.auth.exceptions.TooManyAttemptsException
import com.anima.features.auth.repositories.RefreshTokenRepository
import com.anima.features.exhibitor.entities.ExhibitorEntity
import com.anima.features.exhibitor.repositories.ExhibitorRepository
import com.anima.features.user.entities.UserEntity
import com.anima.features.user.repositories.UserRepository
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.features.visitor.repositories.VisitorRepository
import com.anima.utils.TokenHashUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.Optional
import java.util.UUID

private class FakeUsers(private val users: List<UserEntity>) : UserRepository {
    override fun findById(id: UUID): Optional<UserEntity> = Optional.ofNullable(users.find { it.id == id })
    override fun findByEmail(email: String): Optional<UserEntity> = Optional.ofNullable(users.find { it.email == email })
    override fun existsByEmail(email: String) = users.any { it.email == email }
    override fun save(user: UserEntity) = user
    override fun deleteById(id: UUID) = Unit
}

// no profile rows, so every test user resolves as an exhibitor
private class FakeVisitors : VisitorRepository {
    override fun findById(id: String): Optional<VisitorEntity> = Optional.empty()
    override fun findByUserId(userId: UUID): Optional<VisitorEntity> = Optional.empty()
    override fun existsByUserId(userId: UUID) = false
    override fun save(visitor: VisitorEntity) = visitor
    override fun deleteById(id: String) = Unit
}

private class FakeExhibitors : ExhibitorRepository {
    override fun findById(id: String): Optional<ExhibitorEntity> = Optional.empty()
    override fun findByUserId(userId: UUID): Optional<ExhibitorEntity> = Optional.empty()
    override fun existsByUserId(userId: UUID) = false
    override fun save(exhibitor: ExhibitorEntity) = exhibitor
    override fun deleteById(id: String) = Unit
}

private class FakeTokens : RefreshTokenRepository {
    val rows = mutableListOf<RefreshTokenEntity>()
    override fun findByTokenHash(tokenHash: String) = rows.find { it.tokenHash == tokenHash }
    override fun findAllByUserId(userId: UUID) = rows.filter { it.user?.id == userId }
    override fun save(token: RefreshTokenEntity): RefreshTokenEntity {
        token.id = UUID.randomUUID()
        rows += token
        return token
    }
    override fun deleteById(id: UUID) { rows.removeIf { it.id == id } }
    override fun deleteAllByUserId(userId: UUID) { rows.removeIf { it.user?.id == userId } }
    override fun revokeByTokenHash(tokenHash: String) { findByTokenHash(tokenHash)?.revoked = true }
    override fun revokeIfActive(tokenHash: String, now: Instant): Boolean {
        val t = findByTokenHash(tokenHash)?.takeIf { !it.revoked && it.expiresAt.isAfter(now) } ?: return false
        t.revoked = true
        return true
    }
    override fun findActiveByUserId(userId: UUID, now: Instant) =
        rows.filter { it.user?.id == userId &&!it.revoked && it.expiresAt.isAfter(now) }.sortedBy { it.createdAt }
    override fun deleteExpired(now: Instant) = 0
}

class AuthServiceTest {
    private val password = "correct horse"
    private val user = UserEntity().apply {
        id = UUID.randomUUID()
        email = "user@example.com"
        passwordHash = BCrypt.withDefaults().hashToString(4, password.toCharArray())
    }
    private val tokens = FakeTokens()
    private val jwt = JwtService("a-test-secret-that-is-long-enough-0123456789", "test", 15)
    private val service = AuthService(FakeUsers(listOf(user)), tokens, jwt, FakeVisitors(), FakeExhibitors(), 30)

    @Test
    fun `login normalizes the email`() {
        val result = service.login("  User@Example.COM ", password)
        assertTrue(result.refreshToken.isNotBlank())
    }

    @Test
    fun `login rejects unknown email and wrong password the same way`() {
        assertThrows<InvalidCredentialsException> { service.login("nobody@example.com", password) }
        assertThrows<InvalidCredentialsException> { service.login(user.email, "wrong") }
    }

    @Test
    fun `login rejects oversized input before hashing`() {
        assertThrows<InvalidCredentialsException> { service.login(user.email, "x".repeat(10_000)) }
    }

    @Test
    fun `email is locked after repeated failures`() {
        repeat(5) { assertThrows<InvalidCredentialsException> { service.login(user.email, "wrong") } }
        assertThrows<TooManyAttemptsException> { service.login(user.email, password) }
    }

    @Test
    fun `refresh rotates the token`() {
        val first = service.login(user.email, password)
        val second = service.refresh(first.refreshToken)
        assertTrue(second.refreshToken != first.refreshToken)
        assertTrue(tokens.findByTokenHash(TokenHashUtil.sha256(first.refreshToken))!!.revoked)
    }

    @Test
    fun `replaying a rotated token revokes every session of the user`() {
        val first = service.login(user.email, password)
        val second = service.refresh(first.refreshToken)

        assertThrows<InvalidRefreshTokenException> { service.refresh(first.refreshToken) }

        assertTrue(tokens.findAllByUserId(user.id!!).isEmpty())
        assertThrows<InvalidRefreshTokenException> { service.refresh(second.refreshToken) }
    }

    @Test
    fun `unknown refresh token is rejected without touching other sessions`() {
        val session = service.login(user.email, password)
        assertThrows<InvalidRefreshTokenException> { service.refresh("nope") }
        assertEquals(1, tokens.findActiveByUserId(user.id!!, Instant.now()).size)
        service.refresh(session.refreshToken)
    }

    @Test
    fun `active refresh tokens are capped per user dropping the oldest`() {
        val first = service.login(user.email, password)
        repeat(12) { service.login(user.email, password) }
        assertEquals(10, tokens.findActiveByUserId(user.id!!, Instant.now()).size)
        assertThrows<InvalidRefreshTokenException> { service.refresh(first.refreshToken) }
    }
}
