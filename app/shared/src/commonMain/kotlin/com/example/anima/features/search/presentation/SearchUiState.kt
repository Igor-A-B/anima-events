package com.example.anima.features.search.presentation

import com.example.anima.features.feed.domain.Event
import com.example.anima.features.search.domain.SearchFilters

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val results: List<Event> = emptyList(),
    val isLoading: Boolean = true,
    val isFiltersSheetVisible: Boolean = false,
    val error: String = "",
) {
    val isEmpty: Boolean = !isLoading && error.isBlank() && results.isEmpty()
    val isPristine: Boolean = query.isBlank() && filters.isDefault
}
