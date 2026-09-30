package com.example.anima.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
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
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// the server answers errors as {"error": "..."}
@Serializable
private data class ErrorBody(val error: String = "")

private val json = Json { ignoreUnknownKeys = true }

private const val REQUEST_TIMEOUT_MS = 15_000L

private fun HttpClientConfig<*>.baseSetup() {
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
        url(apiBaseUrl.trimEnd('/') + "/")
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
            AppLog.e("Http", "request failed before a response", cause)
            throw ApiException(null, cause.message ?: "network error")
        }
    }
}

// a json request body, ContentNegotiation serializes it because of the content type
inline fun <reified T> HttpRequestBuilder.jsonBody(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

// no auth plugin, used for login and refresh so a refresh can't trigger a refresh
fun createPlainClient() = HttpClient { baseSetup() }

// sends the access token, and refreshes it on a 401
fun createApiClient(session: SessionTokens) = HttpClient {
    baseSetup()
    install(Auth) {
        bearer {
            // public endpoints ignore the token, but PARTICIPATING needs it on a plain GET
            sendWithoutRequest { true }
            loadTokens { session.validTokens()?.let { BearerTokens(it.accessToken, it.refreshToken) } }
            refreshTokens { session.refresh()?.let { BearerTokens(it.accessToken, it.refreshToken) } }
        }
    }
}

// what the auth plugin needs from the session, implemented by SessionRepository
interface SessionTokens {
    suspend fun validTokens(): com.anima.features.auth.dtos.TokenResponseDto?
    suspend fun refresh(): com.anima.features.auth.dtos.TokenResponseDto?
}
