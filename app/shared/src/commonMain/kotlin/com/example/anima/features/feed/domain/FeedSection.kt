package com.example.anima.features.feed.domain

import com.anima.features.event.models.Event
import com.anima.features.event.models.FeedSectionType

// one page of a section, nextCursor == null means there is nothing after it
data class FeedPage(
    val events: List<Event>,
    val nextCursor: String? = null,
)

// one feed row
data class FeedSection(
    val type: FeedSectionType,
    val events: List<Event>,
    val nextCursor: String? = null,
)
