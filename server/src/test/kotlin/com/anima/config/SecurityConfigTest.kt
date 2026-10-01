package com.anima.config

import com.anima.features.auth.services.JwtService
import com.anima.features.user.models.AccountType
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.util.Date
import java.util.UUID

// endpoints behind the real security chain, only for this test
@RestController
class ProbeController {
    @GetMapping("/probe/me")
    fun me(auth: Authentication): String = "${auth.name}|${auth.authorities.joinToString(",") { it.authority ?: "" }}"

    @GetMapping("/probe/boom")
    fun boom(): String = throw IllegalStateException("boom")

    data class Body(val name: String)

    @PostMapping("/probe/echo")
    fun echo(@RequestBody body: Body): String = body.name
}

// the security config and the jwt service only, no database
@SpringBootConfiguration
@EnableAutoConfiguration(
    excludeName = [
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    ]
)
@Import(SecurityConfig::class, JwtService::class, ProbeController::class)
class ProbeApp

@SpringBootTest(
    classes = [ProbeApp::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["jwt.secret=$SECRET", "jwt.issuer=$ISSUER", "jwt.access-token-expiration-minutes=15"],
)
class SecurityConfigTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jwtService: JwtService

    private val http = HttpClient.newHttpClient()

    private fun get(path: String, token: String? = null): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI("http://localhost:$port$path")).GET()
        token?.let { request.header("Authorization", "Bearer $it") }
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString())
    }

    private fun post(path: String, body: String, token: String?): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI("http://localhost:$port$path"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
        token?.let { request.header("Authorization", "Bearer $it") }
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString())
    }

    private fun token(type: AccountType, id: UUID = UUID.randomUUID()) = jwtService.generateAccessToken(id, "a@b.c", type)

    private fun signed(issuer: String, expiresAt: Instant, secret: String = SECRET): String =
        JWT.create()
            .withIssuer(issuer)
            .withSubject(UUID.randomUUID().toString())
            .withClaim("accountType", AccountType.EXHIBITOR.name)
            .withExpiresAt(Date.from(expiresAt))
            .sign(Algorithm.HMAC256(secret))

    @Test
    fun `a valid token reaches the endpoint as its subject with the account type role`() {
        val exhibitor = UUID.randomUUID()
        val exhibitorResponse = get("/probe/me", token(AccountType.EXHIBITOR, exhibitor))
        assertEquals(200, exhibitorResponse.statusCode())
        assertEquals("$exhibitor|ROLE_EXHIBITOR", exhibitorResponse.body())

        val visitor = UUID.randomUUID()
        val visitorResponse = get("/probe/me", token(AccountType.VISITOR, visitor))
        assertEquals(200, visitorResponse.statusCode())
        assertEquals("$visitor|ROLE_VISITOR", visitorResponse.body())
    }

    @Test
    fun `a missing or bad token answers 401`() {
        assertEquals(401, get("/probe/me").statusCode())
        assertEquals(401, get("/probe/me", "garbage").statusCode())
        assertEquals(401, get("/probe/me", signed("someone-else", Instant.now().plusSeconds(600))).statusCode())
        assertEquals(401, get("/probe/me", signed(ISSUER, Instant.now().plusSeconds(600), "other-secret")).statusCode())
        assertEquals(401, get("/probe/me", signed(ISSUER, Instant.now().minusSeconds(60))).statusCode())
    }

    @Test
    fun `a server error behind auth answers 500, not 401`() {
        assertEquals(500, get("/probe/boom", token(AccountType.EXHIBITOR)).statusCode())
    }

    @Test
    fun `a malformed body answers 400, not 401`() {
        val token = token(AccountType.EXHIBITOR)
        assertEquals(200, post("/probe/echo", """{"name":"ok"}""", token).statusCode())
        assertEquals(400, post("/probe/echo", "{not json", token).statusCode())
    }
}

private const val SECRET = "test-secret"
private const val ISSUER = "anima-server"
