package com.anima.features.user.routes

import com.anima.features.auth.entities.RefreshTokenEntity
import com.anima.features.auth.repositories.RefreshTokenRepository
import com.anima.features.user.entities.UserEntity
import com.anima.features.user.repositories.UserRepository
import com.anima.features.user.services.UserService
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

private class FakeUsers(private val users: List<UserEntity>) : UserRepository {
    override fun findById(id: UUID): Optional<UserEntity> = Optional.ofNullable(users.find { it.id == id })
    override fun findByEmail(email: String): Optional<UserEntity> = Optional.ofNullable(users.find { it.email == email })
    override fun existsByEmail(email: String) = users.any { it.email == email }
    override fun save(user: UserEntity) = user
    override fun deleteById(id: UUID) = Unit
}

private class NoTokens : RefreshTokenRepository {
    override fun findByTokenHash(tokenHash: String): RefreshTokenEntity? = null
    override fun findAllByUserId(userId: UUID) = emptyList<RefreshTokenEntity>()
    override fun save(token: RefreshTokenEntity) = token
    override fun deleteById(id: UUID) = Unit
    override fun deleteAllByUserId(userId: UUID) = Unit
    override fun revokeByTokenHash(tokenHash: String) = Unit
    override fun revokeIfActive(tokenHash: String, now: Instant) = false
    override fun findActiveByUserId(userId: UUID, now: Instant) = emptyList<RefreshTokenEntity>()
    override fun deleteExpired(now: Instant) = 0
}

class UserRouteTest {
    private val user = UserEntity().apply {
        id = UUID.randomUUID()
        name = "Ana"
        email = "ana@example.com"
        registerDate = LocalDateTime.of(2026, 1, 2, 3, 4, 5)
    }
    private val mvc = MockMvcBuilders.standaloneSetup(UserRoute(UserService(FakeUsers(listOf(user)), NoTokens()))).build()

    private fun auth(id: String, vararg authorities: String) =
        UsernamePasswordAuthenticationToken(id, null, authorities.map { SimpleGrantedAuthority(it) })

    @Test
    fun `me answers the profile of the token user`() {
        mvc.get("/me") { principal = auth(user.id.toString(), "ROLE_VISITOR") }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Ana") }
            jsonPath("$.email") { value("ana@example.com") }
            jsonPath("$.accountType") { value("VISITOR") }
            jsonPath("$.registerDate") { value("2026-01-02T03:04:05") }
        }
    }

    @Test
    fun `unrelated authorities do not break the account type lookup`() {
        mvc.get("/me") { principal = auth(user.id.toString(), "SCOPE_read", "ROLE_UNKNOWN", "ROLE_EXHIBITOR") }.andExpect {
            status { isOk() }
            jsonPath("$.accountType") { value("EXHIBITOR") }
        }
    }

    @Test
    fun `unknown user is an expired session`() {
        mvc.get("/me") { principal = auth(UUID.randomUUID().toString(), "ROLE_VISITOR") }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.error") { exists() }
        }
    }

    @Test
    fun `missing role is an expired session`() {
        mvc.get("/me") { principal = auth(user.id.toString()) }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.error") { exists() }
        }
    }

    @Test
    fun `non uuid subject is an expired session`() {
        mvc.get("/me") { principal = auth("not-a-uuid", "ROLE_VISITOR") }.andExpect {
            status { isUnauthorized() }
        }
    }
}
