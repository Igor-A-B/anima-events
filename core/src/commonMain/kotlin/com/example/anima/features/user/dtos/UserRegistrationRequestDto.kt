package com.example.anima.features.user.dtos

import com.example.anima.shared.constants.UserTypeEnum
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val type: UserTypeEnum,
)
