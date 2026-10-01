package com.example.anima.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import com.example.anima.core.log.AppLog
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.clearAuthTokens
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.serialization.kotlinx.json.json
import com.anima.features.auth.dtos.TokenResponseDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// the server answers errors as {"error": "..."}
@Serializable
private data class ErrorBody(val error: String = "")

private val json = Json { ignoreUnknownKeys = true }

private const val REQUEST_TIMEOUT_MS = 15_000L

private fun HttpClientConfig<*>.baseSetup(baseUrl: String?) {
    expectSuccess = true
    // a timeout reaches the validator below and becomes an ApiException with no status
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
        connectTimeoutMillis = REQUEST_TIMEOUT_MS
        socketTimeoutMillis = REQUEST_TIMEOUT_MS
    }
    install(ContentNegotiation) { json(json) }
    // INFO logs method, url and status, never bodies, so passwords and tokens stay out of the log
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = AppLog.i("Http", message)
        }
        level = LogLevel.INFO
        sanitizeHeader { it == HttpHeaders.Authorization }
    }
    defaultRequest {
        url((baseUrl ?: apiBaseUrl).trimEnd('/') + "/")
        // no default Content-Type: it would override the multipart boundary on uploads, json bodies go through jsonBody()
    }
    // turns every failure into an ApiException the view models can handle
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, _ ->
            if (cause is ResponseException) {
                val message = runCatching { cause.response.body<ErrorBody>().error }.getOrDefault("")
                val status = cause.response.status.value
                AppLog.e("Http", "${cause.response.call.request.url} failed with $status: $message")
                throw ApiException(status, message.ifBlank { cause.message ?: "" })
            }
            if (cause is ApiException) throw cause
            AppLog.e("Http", "request failed before a response", cause)
            throw ApiException(null, cause.message ?: "network error", isTimeout = cause.isTimeout(), cause = cause)
        }
    }
}

// walks the causes, engines wrap the timeout in their own exceptions
private fun Throwable.isTimeout(): Boolean = generateSequence(this) { it.cause }.take(8).any {
    it is HttpRequestTimeoutException || it is ConnectTimeoutException || it is SocketTimeoutException
}

// a json request body, ContentNegotiation serializes it because of the content type
inline fun <reified T> HttpRequestBuilder.jsonBody(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

// no auth plugin, used for login and refresh so a refresh can't trigger a refresh
// engine and baseUrl are only passed by tests, production picks the platform engine and apiBaseUrl
fun createPlainClient(engine: HttpClientEngine? = null, baseUrl: String? = null) =
    if (engine == null) HttpClient { baseSetup(baseUrl) } else HttpClient(engine) { baseSetup(baseUrl) }

// sends the access token, and refreshes it on a 401
fun createApiClient(session: SessionTokens, engine: HttpClientEngine? = null, baseUrl: String? = null): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
        baseSetup(baseUrl)
        install(Auth) {
            bearer {
                // public endpoints ignore the token, but PARTICIPATING needs it on a plain GET
                sendWithoutRequest { true }
                // read the tokens from storage on every request: a cached pair outlives a logout,
                // and the next user would keep sending the previous user's access token
                cacheTokens = false
                loadTokens { session.validTokens()?.toBearer() }
                refreshTokens {
                    // the token this request was rejected with, so a refresh someone else already made is reused
                    val failed = response.call.request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
                    session.refresh(failed)?.toBearer()
                }
            }
        }
    }
    val client = if (engine == null) HttpClient(config) else HttpClient(engine, config)
    // belt and braces: drop whatever the auth plugin holds whenever the signed in user changes
    session.onSessionChanged { client.clearAuthTokens() }
    return client
}

private fun TokenResponseDto.toBearer() = BearerTokens(accessToken, refreshToken)

// what the auth plugin needs from the session, implemented by SessionRepository
interface SessionTokens {
    // the stored tokens, refreshed first when the access token is about to expire
    suspend fun validTokens(): TokenResponseDto?

    // failedAccessToken is the token the server rejected, when storage already holds a different one
    // another request refreshed meanwhile and that pair is returned without calling the server again
    suspend fun refresh(failedAccessToken: String?): TokenResponseDto?

    // called after login, register and logout (also when a refresh ends the session)
    fun onSessionChanged(listener: () -> Unit)
}
