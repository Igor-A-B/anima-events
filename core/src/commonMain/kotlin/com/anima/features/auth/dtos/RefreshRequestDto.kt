package com.anima.features.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(val refreshToken: String)
