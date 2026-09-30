package com.anima.features.event.models

import kotlinx.serialization.Serializable

// response of POST /events/{id}/images
@Serializable
data class EventImage(
    val id: String,
    val url: String,
)
