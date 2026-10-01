package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.auth.dtos.TokenResponseDto

// the auth endpoints, all of them throw ApiException on failure
interface AuthRepository {
    suspend fun login(request: LoginRequestDto): TokenResponseDto
    suspend fun register(request: RegisterRequestDto): TokenResponseDto
    suspend fun refresh(request: RefreshRequestDto): TokenResponseDto
    suspend fun logout(request: RefreshRequestDto)
}
