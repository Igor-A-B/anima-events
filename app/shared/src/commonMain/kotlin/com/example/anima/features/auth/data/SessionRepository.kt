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
class SessionRepository(
    private val auth: AuthRepository,
    private val storage: TokenStorage,
) : SessionTokens {

    private val refreshLock = Mutex()
    private val listeners = mutableListOf<() -> Unit>()
    private val _session = MutableStateFlow(storage.load()?.let(::sessionOf))
    val session: StateFlow<Session?> = _session.asStateFlow()

    // throws ApiException, 401 means wrong email or password
    suspend fun login(email: String, password: String) {
        store(auth.login(LoginRequestDto(email.trim(), password)))
        notifySessionChanged()
    }

    // throws ApiException, 409 means the email is taken, 400 means invalid data
    suspend fun register(request: RegisterRequestDto) {
        store(auth.register(request.copy(name = request.name.trim(), email = request.email.trim())))
        notifySessionChanged()
    }

    // revokes the refresh token on the server, the local session ends even if that call fails
    suspend fun signOut() {
        val refreshToken = storage.load()?.refreshToken
        try {
            if (refreshToken != null) auth.logout(RefreshRequestDto(refreshToken))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // nothing to do, the tokens are dropped below anyway
        } finally {
            logout()
        }
    }

    fun logout() {
        storage.clear()
        _session.value = null
        notifySessionChanged()
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
        if (exp - EXPIRY_MARGIN_SECONDS > Clock.System.now().epochSeconds) return tokens
        // a failed refresh that kept the session (offline, rate limited) still sends the old token,
        // the server answers 401 and the auth plugin tries once more
        return refresh(tokens.accessToken) ?: storage.load()
    }

    // one refresh at a time, refresh tokens rotate so the same one must never be sent twice
    // only a definitive 401/403 from /auth/refresh ends the session, offline, 429 or 5xx keep the tokens
    override suspend fun refresh(failedAccessToken: String?): TokenResponseDto? = refreshLock.withLock {
        val current = storage.load() ?: return null
        // someone else refreshed (or signed in) while this request waited, reuse their tokens
        if (current.accessToken != failedAccessToken) return current
        try {
            auth.refresh(RefreshRequestDto(current.refreshToken)).also(::store)
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiException) {
            if (e.status == 401 || e.status == 403) logout()
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun store(tokens: TokenResponseDto) {
        storage.save(tokens)
        _session.value = sessionOf(tokens)
    }

    private fun sessionOf(tokens: TokenResponseDto): Session? =
        parseJwtClaims(tokens.accessToken)?.let { Session(it.userId, it.accountType) }

    private companion object {
        const val EXPIRY_MARGIN_SECONDS = 30
    }
}
