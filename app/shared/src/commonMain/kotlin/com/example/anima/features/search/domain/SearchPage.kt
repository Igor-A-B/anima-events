package com.example.anima.features.search.domain

import com.anima.features.event.models.Event

// one page of results, nextCursor == null means there is nothing after it
data class SearchPage(
    val events: List<Event>,
    val nextCursor: String? = null,
)
