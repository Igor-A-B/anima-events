package com.example.anima.features.addevent.domain.model

import com.anima.features.event.models.EventCategory

data class EventDraft(
    val photoUri: String? = null,
    val name: String = "",
    // dd/MM/yyyy, what the date picker writes
    val date: String = "",
    // HH:mm
    val time: String = "",
    val location: String = "",
    val address: String = "",
    val city: String = "",
    val category: EventCategory? = null,
    val about: String = "",
    val price: String = "",
    val capacity: String = "",
    // not in the form, kept so an edit does not wipe them (update replaces every field)
    val latitude: Double? = null,
    val longitude: Double? = null,
)
