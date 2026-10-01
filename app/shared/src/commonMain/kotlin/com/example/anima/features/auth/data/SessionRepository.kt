package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.user.models.AccountType
import com.anima.features.auth.dtos.TokenResponseDto
import com.example.anima.core.network.ApiException
import com.example.anima.core.network.SessionTokens
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

data class Session(val userId: String, val accountType: AccountType)

// who is signed in, backed by the tokens on disk
// the auth repository has no auth plugin, so login and refresh never loop
// every write to the stored tokens happens under sessionLock, so a refresh still in flight
// can't overwrite or wipe a session that started after it
class SessionRepository(
    private val auth: AuthRepository,
    private val storage: TokenStorage,
    // epoch millis, tests move it to step over the refresh cooldown
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : SessionTokens {

    private val sessionLock = Mutex()
    private val listeners = mutableListOf<() -> Unit>()
    private val _session = MutableStateFlow(storage.load()?.let(::sessionOf))
    val session: StateFlow<Session?> = _session.asStateFlow()

    // the refresh token whose refresh failed without ending the session (offline, 429, 5xx) and until when
    // it is left alone, so every request doesn't hammer /auth/refresh
    private var cooldownRefreshToken: String? = null
    private var cooldownUntilMillis = 0L

    // throws ApiException, 401 means wrong email or password
    suspend fun login(email: String, password: String) {
        val tokens = auth.login(LoginRequestDto(email.trim(), password))
        sessionLock.withLock { store(tokens) }
        notifySessionChanged()
    }

    // throws ApiException, 409 means the email is taken, 400 means invalid data
    suspend fun register(request: RegisterRequestDto) {
        val tokens = auth.register(request.copy(name = request.name.trim(), email = request.email.trim()))
        sessionLock.withLock { store(tokens) }
        notifySessionChanged()
    }

    // ends the local session first, then revokes the refresh token on the server (a failure there is ignored)
    suspend fun signOut() {
        val refreshToken = sessionLock.withLock {
            storage.load()?.refreshToken.also { clearLocal() }
        }
        notifySessionChanged()
        try {
            if (refreshToken != null) auth.logout(RefreshRequestDto(refreshToken))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // nothing to do, the tokens are already gone
        }
    }

    override fun onSessionChanged(listener: () -> Unit) {
        listeners += listener
    }

    private fun notifySessionChanged() {
        listeners.forEach { it() }
    }

    // refreshes first when the access token is about to expire
    override suspend fun validTokens(): TokenResponseDto? {
        val tokens = storage.load() ?: return null
        val exp = parseJwtClaims(tokens.accessToken)?.expiresAtSeconds ?: 0
        if ((exp - EXPIRY_MARGIN_SECONDS) * 1000 > nowMillis()) return tokens
        // a failed refresh that kept the session (offline, rate limited) still sends the old token,
        // the server answers 401 and the request fails without another refresh during the cooldown
        return refresh(tokens.accessToken) ?: storage.load()
    }

    // one refresh at a time, refresh tokens rotate so the same one must never be sent twice
    // only a definitive 401/403 from /auth/refresh ends the session, offline, 429 or 5xx keep the tokens
    override suspend fun refresh(failedAccessToken: String?): TokenResponseDto? {
        var ended = false
        val result = sessionLock.withLock {
            val current = storage.load() ?: return@withLock null
            if (current.accessToken != failedAccessToken) {
                // someone else refreshed meanwhile: reuse their tokens, but only for the same user,
                // a request made as someone else (or as nobody) must not be replayed as the new user
                return@withLock current.takeIf { failedAccessToken != null && sameUser(failedAccessToken, current.accessToken) }
            }
            if (current.refreshToken == cooldownRefreshToken && nowMillis() < cooldownUntilMillis) return@withLock null
            try {
                val fresh = auth.refresh(RefreshRequestDto(current.refreshToken))
                // compare and set, never write over tokens that are not the ones this refresh started from
                if (storage.load() != current) return@withLock null
                store(fresh)
                fresh
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (storage.load() != current) return@withLock null
                if (e is ApiException && (e.status == 401 || e.status == 403)) {
                    clearLocal()
                    ended = true
                } else {
                    cooldownRefreshToken = current.refreshToken
                    cooldownUntilMillis = nowMillis() + REFRESH_COOLDOWN_MILLIS
                }
                null
            }
        }
        if (ended) notifySessionChanged()
        return result
    }

    private fun sameUser(a: String, b: String): Boolean {
        val first = parseJwtClaims(a)?.userId ?: return false
        return first == parseJwtClaims(b)?.userId
    }

    private fun clearLocal() {
        storage.clear()
        _session.value = null
        cooldownRefreshToken = null
    }

    private fun store(tokens: TokenResponseDto) {
        storage.save(tokens)
        _session.value = sessionOf(tokens)
    }

    private fun sessionOf(tokens: TokenResponseDto): Session? =
        parseJwtClaims(tokens.accessToken)?.let { Session(it.userId, it.accountType) }

    private companion object {
        const val EXPIRY_MARGIN_SECONDS = 30L
        const val REFRESH_COOLDOWN_MILLIS = 15_000L
    }
}
