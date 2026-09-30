package com.anima.features.user.dtos

import com.anima.features.user.models.AccountType
import kotlinx.serialization.Serializable

// registerDate is an ISO-8601 local date time
@Serializable
data class MeResponseDto(
    val name: String,
    val email: String,
    val accountType: AccountType,
    val registerDate: String,
)
