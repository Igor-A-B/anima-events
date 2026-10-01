package com.anima.features.event.dtos

import com.anima.features.event.models.Event
import kotlinx.serialization.Serializable

// nextCursor is opaque, null means there is nothing after this page
@Serializable
data class EventPageDto(
    val items: List<Event>,
    val nextCursor: String? = null,
)
