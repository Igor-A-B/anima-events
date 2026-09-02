package com.example.anima.features.user.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val registerDate: LocalDateTime,
)