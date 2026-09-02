package com.example.anima.features.event.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val description: String,
    val detail: String,
    val dateTime: LocalDateTime,
    val exhibitorId: String,
    val addressId: String,
)