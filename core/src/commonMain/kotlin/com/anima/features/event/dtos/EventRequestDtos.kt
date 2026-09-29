package com.anima.features.event.dtos

import com.anima.features.event.models.EventCategory
import kotlinx.serialization.Serializable

// startsAt is ISO local date time, e.g. 2026-10-05T20:00:00
@Serializable
data class CreateEventRequestDto(
    val title: String,
    val category: EventCategory,
    val venue: String,
    val city: String,
    val startsAt: String,
    val description: String = "",
    val price: Double? = null,
    val capacity: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

// full replace, same fields as create
typealias UpdateEventRequestDto = CreateEventRequestDto
