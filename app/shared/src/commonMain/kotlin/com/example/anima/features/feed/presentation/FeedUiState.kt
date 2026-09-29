package com.example.anima.features.feed.presentation

import com.anima.features.event.models.EventCategory
import com.example.anima.features.feed.domain.FeedSection
import com.example.anima.features.feed.domain.FeedSectionType

// sections land one by one, so the feed shows what it has while the rest still loads
data class FeedUiState(
    val sections: List<FeedSection> = emptyList(),
    val pending: Set<FeedSectionType> = FeedSectionType.entries.toSet(),
    val failed: Set<FeedSectionType> = emptySet(),
    val selectedCategory: EventCategory? = null,
) {
    // spinner only until the first section arrives
    val isLoading: Boolean = pending.isNotEmpty() && sections.isEmpty()
    val hasError: Boolean = pending.isEmpty() && sections.isEmpty() && failed.isNotEmpty()
    val isEmpty: Boolean = !isLoading && !hasError && sections.isEmpty()
}
