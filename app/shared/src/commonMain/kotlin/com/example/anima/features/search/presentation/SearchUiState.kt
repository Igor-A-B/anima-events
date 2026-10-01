package com.example.anima.features.search.presentation

import com.anima.features.event.models.Event
import com.example.anima.features.search.domain.SearchFilters

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val results: List<Event> = emptyList(),
    val nextCursor: String? = null,
    val isLoading: Boolean = true,
    // pull to refresh running, unlike isLoading the results stay as they are
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isFiltersSheetVisible: Boolean = false,
    val error: String = "",
) {
    val isEmpty: Boolean = !isLoading && error.isBlank() && results.isEmpty()
    val isPristine: Boolean = query.isBlank() && filters.isDefault

    // results come in pages, so with more to load the count is only a lower bound
    val resultCountLabel: String = if (nextCursor != null) "${results.size}+" else results.size.toString()
}
