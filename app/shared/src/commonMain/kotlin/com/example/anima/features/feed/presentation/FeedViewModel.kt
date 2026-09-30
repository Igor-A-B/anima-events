package com.example.anima.features.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.FeedSectionType
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.domain.FeedPage
import com.example.anima.features.feed.domain.FeedSection
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
    private val moreJobs = mutableMapOf<FeedSectionType, Job>()

    init {
        loadFeed()

        // subscribing or cancelling on the detail screen changes the participating row
        viewModelScope.launch {
            subscriptions.subscriptions.drop(1).collect { loadSection(FeedSectionType.PARTICIPATING) }
        }
    }

    fun onCategorySelected(category: EventCategory?) {
        if (category == _uiState.value.selectedCategory) return

        moreJobs.values.forEach(Job::cancel)
        _uiState.update {
            it.copy(selectedCategory = category, sections = emptyList(), failed = emptySet(), loadingMore = emptySet())
        }
        loadFeed()
    }

    fun loadFeed() {
        FeedSectionType.entries.forEach(::loadSection)
    }

    // every section is searched on its own
    private fun loadSection(type: FeedSectionType) {
        jobs[type]?.cancel()
        moreJobs[type]?.cancel()
        _uiState.update { it.copy(pending = it.pending + type, failed = it.failed - type, loadingMore = it.loadingMore - type) }

        jobs[type] = viewModelScope.launch {
            runCatching { repository.getSection(type, _uiState.value.selectedCategory) }
                .onSuccess { page ->
                    _uiState.update { it.copy(pending = it.pending - type, sections = it.sections.with(type, page)) }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _uiState.update { it.copy(pending = it.pending - type, failed = it.failed + type) }
                }
        }
    }

    // next page of one section, called by the row when the user scrolls near its end
    fun loadMore(type: FeedSectionType) {
        val state = _uiState.value
        val cursor = state.sections.firstOrNull { it.type == type }?.nextCursor ?: return
        if (type in state.pending || type in state.loadingMore) return

        _uiState.update { it.copy(loadingMore = it.loadingMore + type) }

        moreJobs[type] = viewModelScope.launch {
            runCatching { repository.getSection(type, state.selectedCategory, cursor) }
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(loadingMore = it.loadingMore - type, sections = it.sections.appended(type, page))
                    }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable
                    // the row keeps what it has and asks again on the next scroll
                    _uiState.update { it.copy(loadingMore = it.loadingMore - type) }
                }
        }
    }

    // keeps the sections in enum order and drops empty ones
    private fun List<FeedSection>.with(type: FeedSectionType, page: FeedPage) =
        (filter { it.type != type } + listOfNotNull(FeedSection(type, page.events, page.nextCursor).takeIf { page.events.isNotEmpty() }))
            .sortedBy { it.type.ordinal }

    private fun List<FeedSection>.appended(type: FeedSectionType, page: FeedPage) = map { section ->
        if (section.type != type) section
        else section.copy(
            events = section.events + page.events.filter { new -> section.events.none { it.id == new.id } },
            nextCursor = page.nextCursor,
        )
    }
}
