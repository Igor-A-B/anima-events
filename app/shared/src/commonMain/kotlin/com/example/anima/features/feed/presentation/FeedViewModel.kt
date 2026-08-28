package com.example.anima.features.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.data.MockFeedRepository
import com.example.anima.features.feed.domain.EventCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: FeedRepository = MockFeedRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun onCategorySelected(category: EventCategory?) {
        if (category == _uiState.value.selectedCategory) return

        _uiState.update { it.copy(selectedCategory = category) }
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }

            runCatching { repository.getSections(_uiState.value.selectedCategory) }
                .onSuccess { sections ->
                    _uiState.update { it.copy(isLoading = false, sections = sections) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "unknown error",
                        )
                    }
                }
        }
    }
}
