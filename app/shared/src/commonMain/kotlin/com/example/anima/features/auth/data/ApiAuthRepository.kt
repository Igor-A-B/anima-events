package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.auth.dtos.TokenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import com.example.anima.core.network.jsonBody

// client must have no auth plugin, so login and refresh never loop
class ApiAuthRepository(private val plainClient: HttpClient) : AuthRepository {

    override suspend fun login(request: LoginRequestDto): TokenResponseDto =
        plainClient.post("auth/login") { jsonBody(request) }.body()

    override suspend fun register(request: RegisterRequestDto): TokenResponseDto =
        plainClient.post("auth/register") { jsonBody(request) }.body()

    override suspend fun refresh(request: RefreshRequestDto): TokenResponseDto =
        plainClient.post("auth/refresh") { jsonBody(request) }.body()

    override suspend fun logout(request: RefreshRequestDto) {
        plainClient.post("auth/logout") { jsonBody(request) }
    }
}
