package com.example.anima.features.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.domain.FeedSection
import com.example.anima.features.feed.domain.FeedSectionType
import com.example.anima.features.subscription.data.SubscriptionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: FeedRepository,
    subscriptions: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val jobs = mutableMapOf<FeedSectionType, Job>()

    init {
        loadFeed()

        // subscribing or cancelling on the detail screen changes the participating row
        viewModelScope.launch {
            subscriptions.subscriptions.drop(1).collect { loadSection(FeedSectionType.PARTICIPATING) }
        }
    }

    fun onCategorySelected(category: EventCategory?) {
        if (category == _uiState.value.selectedCategory) return

        _uiState.update { it.copy(selectedCategory = category, sections = emptyList(), failed = emptySet()) }
        loadFeed()
    }

    fun loadFeed() {
        FeedSectionType.entries.forEach(::loadSection)
    }

    // every section is searched on its own
    private fun loadSection(type: FeedSectionType) {
        jobs[type]?.cancel()
        _uiState.update { it.copy(pending = it.pending + type, failed = it.failed - type) }

        jobs[type] = viewModelScope.launch {
            runCatching { repository.getSection(type, _uiState.value.selectedCategory) }
                .onSuccess { events ->
                    _uiState.update { it.copy(pending = it.pending - type, sections = it.sections.with(type, events)) }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _uiState.update { it.copy(pending = it.pending - type, failed = it.failed + type) }
                }
        }
    }

    // keeps the sections in enum order and drops empty ones
    private fun List<FeedSection>.with(type: FeedSectionType, events: List<Event>) =
        (filter { it.type != type } + listOfNotNull(FeedSection(type, events).takeIf { events.isNotEmpty() }))
            .sortedBy { it.type.ordinal }
}
