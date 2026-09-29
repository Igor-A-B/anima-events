package com.example.anima.features.feed.presentation

import com.anima.features.event.models.EventCategory
import com.example.anima.features.feed.domain.FeedSection

data class FeedUiState(
    val isLoading: Boolean = true,
    val sections: List<FeedSection> = emptyList(),
    val selectedCategory: EventCategory? = null,
    val error: String = "",
) {
    val isEmpty: Boolean = !isLoading && sections.all { it.events.isEmpty() }
}
