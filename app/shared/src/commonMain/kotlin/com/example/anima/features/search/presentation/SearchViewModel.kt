package com.example.anima.features.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.EventCategory
import com.example.anima.core.events.EventChanges
import com.example.anima.features.search.data.SearchRepository
import com.anima.features.event.models.DateFilter
import com.anima.features.event.models.PriceFilter
import com.example.anima.features.search.domain.SearchFilters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository,
    eventChanges: EventChanges,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var moreJob: Job? = null

    init {
        search(debounce = false)

        viewModelScope.launch {
            eventChanges.version.drop(1).collect { search(debounce = false) }
        }
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

    // pull to refresh: same query and filters right away, the results stay while it runs
    fun refresh() {
        val state = _uiState.value
        if (state.isRefreshing || state.isLoading) return
        search(debounce = false, refresh = true)
    }

    private fun updateFilters(transform: (SearchFilters) -> SearchFilters) {
        _uiState.update { state -> state.copy(filters = transform(state.filters)) }
        // taps are deliberate, so they apply straight away — only typing waits
        search(debounce = false)
    }

    private fun search(debounce: Boolean, refresh: Boolean = false) {
        searchJob?.cancel()
        // a page of the previous search must not land on the new results
        moreJob?.cancel()
        // typing or a filter cancels a running refresh, so its indicator stops here too
        _uiState.update { it.copy(isRefreshing = refresh) }

        searchJob = viewModelScope.launch {
            if (debounce) delay(QUERY_DEBOUNCE_MILLIS)

            if (refresh) {
                _uiState.update { it.copy(isLoadingMore = false) }
            } else {
                _uiState.update { it.copy(isLoading = true, isLoadingMore = false, error = "") }
            }

            val state = _uiState.value

            runCatching { repository.search(state.query, state.filters) }
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            results = page.events,
                            nextCursor = page.nextCursor,
                            error = "",
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable

                    // a failed refresh keeps the results it already had
                    if (refresh) {
                        _uiState.update {
                            it.copy(
                                isRefreshing = false,
                                error = if (it.results.isEmpty()) throwable.message ?: "unknown error" else it.error,
                            )
                        }
                        return@onFailure
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = emptyList(),
                            nextCursor = null,
                            error = throwable.message ?: "unknown error",
                        )
                    }
                }
        }
    }

    // next page, called by the grid when the user scrolls near its end
    fun loadMore() {
        val state = _uiState.value
        val cursor = state.nextCursor ?: return
        // during a refresh the cursor is about to be replaced
        if (state.isLoading || state.isLoadingMore || state.isRefreshing) return

        _uiState.update { it.copy(isLoadingMore = true) }

        moreJob = viewModelScope.launch {
            runCatching { repository.search(state.query, state.filters, cursor) }
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            results = it.results + page.events.filter { new -> it.results.none { old -> old.id == new.id } },
                            nextCursor = page.nextCursor,
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable
                    // the grid keeps what it has and asks again on the next scroll
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
        }
    }

    private companion object {
        const val QUERY_DEBOUNCE_MILLIS = 350L
    }
}
