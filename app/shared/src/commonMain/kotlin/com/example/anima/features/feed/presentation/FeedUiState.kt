package com.example.anima.features.feed.presentation

import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.FeedSectionType
import com.example.anima.features.feed.domain.FeedSection

// sections land one by one, so the feed shows what it has while the rest still loads
data class FeedUiState(
    val sections: List<FeedSection> = emptyList(),
    val pending: Set<FeedSectionType> = FeedSectionType.entries.toSet(),
    val failed: Set<FeedSectionType> = emptySet(),
    // sections fetching their next page
    val loadingMore: Set<FeedSectionType> = emptySet(),
    val selectedCategory: EventCategory? = null,
    // pull to refresh running, the current feed stays while every section reloads
    val isRefreshing: Boolean = false,
) {
    // spinner only until the first section arrives
    val isLoading: Boolean = !isRefreshing && pending.isNotEmpty() && sections.isEmpty()
    // a refresh keeps the error message until it succeeds
    val hasError: Boolean = (pending.isEmpty() || isRefreshing) && sections.isEmpty() && failed.isNotEmpty()
    val isEmpty: Boolean = !isLoading && !hasError && sections.isEmpty()
}
