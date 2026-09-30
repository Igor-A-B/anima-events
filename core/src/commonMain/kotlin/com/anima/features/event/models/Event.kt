package com.anima.features.event.models

import kotlinx.serialization.Serializable

// event categories
// labels are resolved in the UI
enum class EventCategory {
    MUSIC,
    ARTS,
    CUISINE,
    PARTY,
    SPORT,
    THEATER,
    CINEMA,
    TECH,
}

// what the card shows on the top right: date, live or ended
enum class EventStatus {
    UPCOMING,
    OCCURRING,
    FINISHED,
}

@Serializable
data class Event(
    val id: String,
    val title: String,
    val category: EventCategory,
    val venue: String,
    val city: String,
    val dateLabel: String,
    val timeLabel: String,
    val price: String? = null,
    val distanceLabel: String? = null,
    val attendees: Int = 0,
    val status: EventStatus = EventStatus.UPCOMING,
    val coverSeed: Int = 0,
    val description: String = "",
    val organizerName: String = "",
    val address: String? = null,
    val imageUrl: String? = null,
    // user id of the organizer, lets the app tell if the logged in user curates the event
    val curatorId: String = "",
    // oldest first, the first one is the cover
    val imageUrls: List<String> = emptyList(),
)