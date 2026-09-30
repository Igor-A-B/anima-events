package com.anima.features.user.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequestDto(val currentPassword: String, val newPassword: String)
