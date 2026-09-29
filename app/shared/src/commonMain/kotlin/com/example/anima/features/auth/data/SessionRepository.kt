package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.TokenResponseDto
import com.example.anima.core.network.ApiException
import com.example.anima.core.network.SessionTokens
import com.example.anima.features.auth.presentation.register.AccountType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

data class Session(val userId: String, val accountType: AccountType)

// who is signed in, backed by the tokens on disk
// plainClient has no auth plugin, so login and refresh never loop
class SessionRepository(
    private val plainClient: HttpClient,
    private val storage: TokenStorage,
) : SessionTokens {

    private val refreshLock = Mutex()
    private val _session = MutableStateFlow(storage.load()?.let(::sessionOf))
    val session: StateFlow<Session?> = _session.asStateFlow()

    // throws ApiException, 401 means wrong email or password
    suspend fun login(email: String, password: String) {
        val tokens = plainClient.post("auth/login") {
            setBody(LoginRequestDto(email.trim(), password))
        }.body<TokenResponseDto>()
        store(tokens)
    }

    fun logout() {
        storage.clear()
        _session.value = null
    }

    // refreshes first when the access token is about to expire
    override suspend fun validTokens(): TokenResponseDto? {
        val tokens = storage.load() ?: return null
        val exp = parseJwtClaims(tokens.accessToken)?.expiresAtSeconds ?: 0
        return if (exp - EXPIRY_MARGIN_SECONDS > Clock.System.now().epochSeconds) tokens else refresh()
    }

    // a bad refresh token ends the session
    override suspend fun refresh(): TokenResponseDto? = refreshLock.withLock {
        val current = storage.load() ?: return null
        try {
            plainClient.post("auth/refresh") {
                setBody(RefreshRequestDto(current.refreshToken))
            }.body<TokenResponseDto>().also(::store)
        } catch (e: ApiException) {
            if (e.status == 401) logout()
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
