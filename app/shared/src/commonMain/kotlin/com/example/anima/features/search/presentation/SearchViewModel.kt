package com.example.anima.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.search.data.MockSearchRepository
import com.example.anima.features.search.data.SearchRepository
import com.example.anima.features.search.domain.DateFilter
import com.example.anima.features.search.domain.PriceFilter
import com.example.anima.features.search.domain.SearchFilters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository = MockSearchRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        search(debounce = false)
    }

    fun onQueryChange(query: String) {
        if (query == _uiState.value.query) return

        _uiState.update { it.copy(query = query) }
        search(debounce = true)
    }

    fun onClearQuery() {
        if (_uiState.value.query.isEmpty()) return

        _uiState.update { it.copy(query = "") }
        search(debounce = false)
    }

    fun onToggleCategory(category: EventCategory) {
        updateFilters { filters -> filters.toggleCategory(category) }
    }

    fun onPriceFilterChange(price: PriceFilter) {
        updateFilters { filters -> filters.copy(price = price) }
    }

    fun onDateFilterChange(date: DateFilter) {
        updateFilters { filters -> filters.copy(date = date) }
    }

    fun onClearFilters() {
        if (_uiState.value.filters.isDefault) return

        _uiState.update { it.copy(filters = SearchFilters()) }
        search(debounce = false)
    }

    fun onFiltersSheetVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(isFiltersSheetVisible = visible) }
    }

    fun retry() = search(debounce = false)

    private fun updateFilters(transform: (SearchFilters) -> SearchFilters) {
        _uiState.update { state -> state.copy(filters = transform(state.filters)) }
        // taps are deliberate, so they apply straight away — only typing waits
        search(debounce = false)
    }

    private fun search(debounce: Boolean) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            if (debounce) delay(QUERY_DEBOUNCE_MILLIS)

            _uiState.update { it.copy(isLoading = true, error = "") }

            val state = _uiState.value

            runCatching { repository.search(state.query, state.filters) }
                .onSuccess { results ->
                    _uiState.update { it.copy(isLoading = false, results = results) }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = emptyList(),
                            error = throwable.message ?: "unknown error",
                        )
                    }
                }
        }
    }

    private companion object {
        const val QUERY_DEBOUNCE_MILLIS = 350L
    }
}
