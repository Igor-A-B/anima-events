package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.auth.dtos.TokenResponseDto
import com.anima.features.user.models.AccountType
import com.example.anima.core.error.AppError
import com.example.anima.core.error.ErrorContext
import com.example.anima.core.error.toAppError
import com.example.anima.core.network.ApiException
import com.example.anima.core.network.createApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class SessionAuthTest {

    private class MemoryTokenStorage : TokenStorage {
        var tokens: TokenResponseDto? = null
        override fun load() = tokens
        override fun save(tokens: TokenResponseDto) {
            this.tokens = tokens
        }
        override fun clear() {
            tokens = null
        }
    }

    // a tiny auth server: rotating refresh tokens, a replayed one is rejected like the real server does
    private class FakeAuth : AuthRepository {
        private val users = mapOf(
            "visitor@test.com" to ("visitor-id" to AccountType.VISITOR),
            "exhibitor@test.com" to ("exhibitor-id" to AccountType.EXHIBITOR),
        )
        private val liveRefreshTokens = mutableMapOf<String, Pair<String, AccountType>>()
        private var counter = 0

        var refreshCalls = 0
        val refreshTokensSent = mutableListOf<String>()
        var refreshFailure: Throwable? = null
        var refreshDelayMs = 0L
        // when set, refresh waits for it, so other things can happen while it is in flight
        var gate: CompletableDeferred<Unit>? = null
        var nextExpiry: () -> Long = { now() + 900 }

        fun issue(user: Pair<String, AccountType>): TokenResponseDto {
            counter++
            val refresh = "refresh-$counter"
            liveRefreshTokens[refresh] = user
            return TokenResponseDto(jwt(user.first, user.second, nextExpiry(), counter), refresh)
        }

        override suspend fun login(request: LoginRequestDto): TokenResponseDto =
            issue(users[request.email] ?: throw ApiException(401, "Invalid email or password"))

        override suspend fun register(request: RegisterRequestDto): TokenResponseDto =
            issue("new-id" to request.accountType)

        override suspend fun refresh(request: RefreshRequestDto): TokenResponseDto {
            refreshCalls++
            refreshTokensSent += request.refreshToken
            if (refreshDelayMs > 0) delay(refreshDelayMs)
            gate?.await()
            refreshFailure?.let { throw it }
            val user = liveRefreshTokens.remove(request.refreshToken)
                ?: throw ApiException(401, "Refresh token inválido ou expirado")
            return issue(user)
        }

        override suspend fun logout(request: RefreshRequestDto) {
            liveRefreshTokens.remove(request.refreshToken)
        }
    }

    private class Fixture {
        val storage = MemoryTokenStorage()
        val auth = FakeAuth()
        var nowMillis = Clock.System.now().toEpochMilliseconds()
        val session = SessionRepository(auth, storage) { nowMillis }

        // tokens the api answers 401 to, like a revoked or expired one
        val rejected = mutableSetOf<String>()
        val sentTokens = mutableListOf<String?>()

        val client: HttpClient = createApiClient(
            session,
            MockEngine { request ->
                val token = request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
                sentTokens += token
                val ok = token != null && token !in rejected
                respond(
                    content = if (ok) "{}" else """{"error":"Unauthorized"}""",
                    status = if (ok) HttpStatusCode.OK else HttpStatusCode.Unauthorized,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            },
            baseUrl = "http://test.local",
        )
    }

    @Test
    fun theNextUserNeverSendsThePreviousUsersToken() = runTest {
        val f = Fixture()

        f.session.login("visitor@test.com", "pw")
        val visitorToken = f.storage.tokens!!.accessToken
        f.client.get("events")
        assertEquals(visitorToken, f.sentTokens.last())

        f.session.signOut()
        f.session.login("exhibitor@test.com", "pw")
        val exhibitorToken = f.storage.tokens!!.accessToken
        f.client.get("events")

        assertEquals(exhibitorToken, f.sentTokens.last())
        assertEquals(AccountType.EXHIBITOR, parseJwtClaims(f.sentTokens.last()!!)?.accountType)
    }

    @Test
    fun afterLogoutNoTokenIsSent() = runTest {
        val f = Fixture()
        f.session.login("visitor@test.com", "pw")
        f.client.get("events")

        f.session.signOut()
        assertFailsWith<ApiException> { f.client.get("events") }
        assertNull(f.sentTokens.last())
    }

    @Test
    fun aRejectedTokenIsRefreshedOnceAndTheCallRetried() = runTest {
        val f = Fixture()
        f.session.login("visitor@test.com", "pw")
        val old = f.storage.tokens!!.accessToken
        f.rejected += old

        f.client.get("events")

        val fresh = f.storage.tokens!!.accessToken
        assertEquals(1, f.auth.refreshCalls)
        assertTrue(fresh != old)
        assertEquals<List<String?>>(listOf(old, fresh), f.sentTokens)
    }

    @Test
    fun anExpiringTokenIsRefreshedBeforeTheCall() = runTest {
        val f = Fixture()
        f.auth.nextExpiry = { now() - 10 }
        f.session.login("visitor@test.com", "pw")
        val old = f.storage.tokens!!.accessToken
        f.auth.nextExpiry = { now() + 900 }

        f.client.get("events")

        assertEquals(1, f.auth.refreshCalls)
        assertEquals<List<String?>>(listOf(f.storage.tokens!!.accessToken), f.sentTokens)
        assertTrue(f.sentTokens.single() != old)
    }

    @Test
    fun concurrentRejectionsRefreshOnceAndNeverReplayARefreshToken() = runTest {
        val f = Fixture()
        f.session.login("visitor@test.com", "pw")
        val old = f.storage.tokens!!.accessToken
        f.rejected += old
        f.auth.refreshDelayMs = 50

        (1..5).map { async { f.client.get("events") } }.awaitAll()

        assertEquals(1, f.auth.refreshCalls)
        assertEquals(f.auth.refreshTokensSent.distinct(), f.auth.refreshTokensSent)
        val fresh = f.storage.tokens!!.accessToken
        // every retry went out with the rotated token
        assertEquals(5, f.sentTokens.count { it == fresh })
        assertNotNull(f.session.session.value)
    }

    @Test
    fun aRefreshForAnAlreadyRotatedTokenReusesTheStoredOne() = runTest {
        val f = Fixture()
        f.session.login("visitor@test.com", "pw")
        val old = f.storage.tokens!!.accessToken

        val first = f.session.refresh(old)
        val second = f.session.refresh(old)

        assertEquals(1, f.auth.refreshCalls)
        assertEquals(first, second)
    }

    @Test
    fun aRejectedRefreshEndsTheSessionAndReadsAsSessionExpired() = runTest {
        val f = Fixture()
        f.session.login("exhibitor@test.com", "pw")
        f.rejected += f.storage.tokens!!.accessToken
        f.auth.refreshFailure = ApiException(401, "Refresh token inválido ou expirado")

        val error = assertFailsWith<ApiException> { f.client.get("events") }

        assertEquals(401, error.status)
        assertEquals(AppError.SESSION_EXPIRED, error.toAppError(ErrorContext.CREATE_EVENT))
        assertNull(f.storage.tokens)
        assertNull(f.session.session.value)
    }

    @Test
    fun aRefreshThatFailsForNetworkOrRateLimitKeepsTheTokens() = runTest {
        for (failure in listOf(ApiException(null, "offline"), ApiException(429, "Too many requests, try again later"))) {
            val f = Fixture()
            f.session.login("visitor@test.com", "pw")
            val tokens = f.storage.tokens
            f.rejected += tokens!!.accessToken
            f.auth.refreshFailure = failure

            assertFailsWith<ApiException> { f.client.get("events") }

            assertEquals(1, f.auth.refreshCalls, "one request, one refresh after $failure")
            assertEquals(tokens, f.storage.tokens, "tokens dropped after $failure")
            assertNotNull(f.session.session.value)
        }
    }

    @Test
    fun anExpiredTokenWhoseRefreshFailsIsNotRefreshedAgainDuringTheCooldown() = runTest {
        for (failure in listOf(ApiException(null, "offline"), ApiException(429, "slow down"), ApiException(503, ""))) {
            val f = Fixture()
            f.auth.nextExpiry = { now() - 10 }
            f.session.login("visitor@test.com", "pw")
            val tokens = f.storage.tokens!!
            f.rejected += tokens.accessToken
            f.auth.refreshFailure = failure

            assertFailsWith<ApiException> { f.client.get("events") }
            assertEquals(1, f.auth.refreshCalls, "first request after $failure")

            assertFailsWith<ApiException> { f.client.get("events") }
            assertEquals(1, f.auth.refreshCalls, "second request inside the cooldown after $failure")
            assertEquals(tokens, f.storage.tokens)

            // once the cooldown is over the server is asked again
            f.nowMillis += 20_000
            f.auth.refreshFailure = null
            f.auth.nextExpiry = { now() + 900 }
            f.client.get("events")
            assertEquals(2, f.auth.refreshCalls)
        }
    }

    @Test
    fun aRefreshInFlightDuringAnAccountSwitchNeverTouchesTheNewSession() = runTest {
        for (refreshFails in listOf(true, false)) {
            val f = Fixture()
            f.session.login("visitor@test.com", "pw")
            val visitorToken = f.storage.tokens!!.accessToken
            val gate = CompletableDeferred<Unit>()
            f.auth.gate = gate
            if (refreshFails) f.auth.refreshFailure = ApiException(401, "Refresh token inválido ou expirado")

            val refresh = launch { f.session.refresh(visitorToken) }
            runCurrent()
            assertEquals(1, f.auth.refreshCalls)
            val switch = launch {
                f.session.signOut()
                f.session.login("exhibitor@test.com", "pw")
            }
            runCurrent()
            gate.complete(Unit)
            joinAll(refresh, switch)

            assertEquals(AccountType.EXHIBITOR, parseJwtClaims(f.storage.tokens!!.accessToken)?.accountType, "refresh failed: $refreshFails")
            assertEquals(AccountType.EXHIBITOR, f.session.session.value?.accountType)
        }
    }

    @Test
    fun aRefreshOnlyWritesOverTheTokensItStartedFrom() = runTest {
        for (refreshFails in listOf(true, false)) {
            val f = Fixture()
            f.session.login("visitor@test.com", "pw")
            val visitorToken = f.storage.tokens!!.accessToken
            val exhibitorTokens = f.auth.issue("exhibitor-id" to AccountType.EXHIBITOR)
            val gate = CompletableDeferred<Unit>()
            f.auth.gate = gate
            if (refreshFails) f.auth.refreshFailure = ApiException(401, "Refresh token inválido ou expirado")

            val refresh = async { f.session.refresh(visitorToken) }
            runCurrent()
            // a write that did not go through the session, the refresh must leave it alone
            f.storage.tokens = exhibitorTokens
            gate.complete(Unit)

            assertNull(refresh.await())
            assertEquals(exhibitorTokens, f.storage.tokens, "refresh failed: $refreshFails")
        }
    }

    @Test
    fun anOldRequestIsNotReplayedAsTheNextUser() = runTest {
        val f = Fixture()
        f.session.login("visitor@test.com", "pw")
        val visitorToken = f.storage.tokens!!.accessToken
        f.session.signOut()

        assertNull(f.session.refresh(visitorToken), "no session at all")

        f.session.login("exhibitor@test.com", "pw")
        assertNull(f.session.refresh(visitorToken), "another user is signed in")
        assertNull(f.session.refresh(null), "the request carried no token")
        assertEquals(0, f.auth.refreshCalls)
    }

    private companion object {
        fun now() = Clock.System.now().epochSeconds

        // unsigned, the client only reads the claims
        fun jwt(sub: String, type: AccountType, exp: Long, jti: Int): String {
            fun part(json: String) = Base64.UrlSafe.encode(json.encodeToByteArray()).trimEnd('=')
            val header = part("""{"alg":"none","typ":"JWT"}""")
            val payload = part("""{"sub":"$sub","accountType":"${type.name}","exp":$exp,"jti":"$jti"}""")
            return "$header.$payload.sig"
        }
    }
}
