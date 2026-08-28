package com.example.anima.features.feed.domain

// section for events
enum class FeedSectionType {
    HAPPENING_NOW,
    NEARBY,
    RECOMMENDED,
    PARTICIPATING,
}

// one feed row
data class FeedSection(
    val type: FeedSectionType,
    val events: List<Event>,
)
