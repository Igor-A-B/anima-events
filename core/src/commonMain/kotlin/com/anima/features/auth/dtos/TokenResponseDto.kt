package com.anima.features.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(val accessToken: String, val refreshToken: String)
