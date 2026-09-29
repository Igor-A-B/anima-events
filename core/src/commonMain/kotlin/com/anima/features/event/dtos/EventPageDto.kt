package com.anima.features.event.dtos

import com.anima.features.event.models.Event
import kotlinx.serialization.Serializable

@Serializable
data class EventPageDto(
    val items: List<Event>,
    val page: Int,
    val size: Int,
    val total: Long,
)
