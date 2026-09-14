package com.example.anima.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anima.features.profile.data.repository.MockProfileRepository
import com.example.anima.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = MockProfileRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }

            runCatching { repository.getProfile() }
                .onSuccess { profile ->
                    _uiState.update { it.copy(isLoading = false, profile = profile) }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "unknown error",
                        )
                    }
                }
        }
    }

    fun onToggleDocumentVisibility() {
        _uiState.update { it.copy(isDocumentVisible = !it.isDocumentVisible) }
    }

    fun onSeeAllEvents() {
        val state = _uiState.value
        if (state.isLoadingEvents || !state.hasMoreEvents) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEvents = true) }

            runCatching { repository.getAllCreatedEvents() }
                .onSuccess { events ->
                    _uiState.update { current ->
                        current.copy(
                            isLoadingEvents = false,
                            isShowingAllEvents = true,
                            profile = current.profile?.copy(createdEvents = events),
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) throw throwable

                    _uiState.update { it.copy(isLoadingEvents = false) }
                }
        }
    }
}
