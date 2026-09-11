package com.example.anima.features.addevent.domain.model

import com.example.anima.features.feed.domain.EventCategory

data class EventDraft(
    val photoUri: String? = null,
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val address: String = "",
    val category: EventCategory? = null,
    val about: String = "",
    val price: String = "",
    val capacity: String = "",
)