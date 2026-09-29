package com.anima.features.auth.dtos

import com.anima.features.user.models.AccountType
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val accountType: AccountType,
)
