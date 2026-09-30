package com.example.anima.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anima.core.error.toAppError
import com.example.anima.core.log.AppLog
import com.example.anima.features.addevent.data.ExhibitorEventRepository
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val session: SessionRepository,
    private val exhibitorEvents: ExhibitorEventRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(accountType = session.session.value?.accountType ?: ProfileUiState().accountType)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadProfile()
        if (_uiState.value.isExhibitor) {
            // the form updates the list after a create, edit or delete, the profile just follows it
            exhibitorEvents.myEvents
                .onEach { events -> _uiState.update { it.copy(createdEvents = events) } }
                .launchIn(viewModelScope)
            loadEvents()
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEvents = true) }
            try {
                exhibitorEvents.refresh()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // the section just stays empty, the rest of the profile still works
                AppLog.e("Profile", "could not load the created events", e)
            } finally {
                _uiState.update { it.copy(isLoadingEvents = false) }
            }
        }
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

    fun onOpenPasswordDialog() {
        _uiState.update { it.copy(isPasswordDialogOpen = true, passwordError = null) }
    }

    fun onDismissPasswordDialog() {
        if (_uiState.value.isChangingPassword) return
        _uiState.update { it.copy(isPasswordDialogOpen = false, passwordError = null) }
    }

    // the server rolls everything back on failure, so the old password keeps working
    // on success every session is ended, this one included, so the user signs in again
    fun onChangePassword(currentPassword: String, newPassword: String, onDone: () -> Unit) {
        if (_uiState.value.isChangingPassword) return

        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, passwordError = null) }
            try {
                repository.changePassword(currentPassword, newPassword)
                _uiState.update { it.copy(isChangingPassword = false, isPasswordDialogOpen = false) }
                session.signOut()
                onDone()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isChangingPassword = false, passwordError = e.toAppError()) }
            }
        }
    }

    fun onLogout(onDone: () -> Unit) {
        if (_uiState.value.isSigningOut) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true) }
            try {
                session.signOut()
            } finally {
                _uiState.update { it.copy(isSigningOut = false) }
            }
            onDone()
        }
    }

    // TODO implement?
    // fun onToggleDocumentVisibility() {
    //     _uiState.update { it.copy(isDocumentVisible = !it.isDocumentVisible) }
    // }
    //
    // fun onSeeAllEvents() { ... loads repository.getAllCreatedEvents() ... }
}
