package com.example.anima.features.feed.domain

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
// TODO: the doc also lists cancelled and postponed
enum class EventStatus {
    UPCOMING,
    OCCURRING,
    FINISHED,
}

// feed event
// TODO: real types when the API lands
data class Event(
    val id: String,
    val title: String,
    val category: EventCategory,
    val venue: String,
    val city: String,
    val dateLabel: String,
    val timeLabel: String,
    // null = free event
    val price: String? = null,
    val distanceLabel: String? = null,
    val attendees: Int = 0,
    val status: EventStatus = EventStatus.UPCOMING,
    // stands in for the cover image: the UI turns it into a gradient
    val coverSeed: Int = 0,
)
