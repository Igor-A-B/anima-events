package com.example.anima.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anima.core.error.ErrorContext
import com.example.anima.core.error.toAppError
import com.example.anima.core.log.AppLog
import com.example.anima.features.addevent.data.ExhibitorEventRepository
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.profile.domain.repository.ProfileRepository
import com.example.anima.features.subscription.data.SubscriptionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val session: SessionRepository,
    private val exhibitorEvents: ExhibitorEventRepository,
    private val subscriptions: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(accountType = session.session.value?.accountType ?: ProfileUiState().accountType)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var eventsJob: Job? = null
    private var observeJob: Job? = null

    init {
        loadProfile()
        loadEvents()
    }

    // the repositories outlive the session, so their lists are only shown after a refresh
    // made by this screen succeeded, otherwise the previous user's events could leak in
    private fun observeEvents() {
        if (observeJob != null) return
        val source = if (_uiState.value.isExhibitor) {
            // the form updates the list after a create, edit or delete, the profile just follows it
            exhibitorEvents.myEvents.map { events -> { state: ProfileUiState -> state.copy(createdEvents = events) } }
        } else {
            // follows subscribe and cancel made elsewhere
            subscriptions.subscriptions.map { list ->
                { state: ProfileUiState -> state.copy(participatingEvents = participatingEvents(list)) }
            }
        }
        observeJob = source
            .onEach { change -> _uiState.update(change) }
            .launchIn(viewModelScope)
    }

    // called when the screen comes back to the front, skipped while a load is already running
    fun onResume() {
        if (eventsJob?.isActive != true) loadEvents()
    }

    fun loadEvents() {
        eventsJob?.cancel()
        eventsJob = viewModelScope.launch {
            // the spinner is only for an empty section, a refresh behind a list stays quiet
            _uiState.update { it.copy(isLoadingEvents = it.myEvents.isEmpty(), eventsError = null) }
            try {
                if (_uiState.value.isExhibitor) exhibitorEvents.refresh() else subscriptions.refresh()
                observeEvents()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // same as the profile load: a cancelled refresh must not flag the section as failed
                currentCoroutineContext().ensureActive()
                // only the section fails, the rest of the profile still works
                AppLog.e("Profile", "could not load the events", e)
                _uiState.update { it.copy(eventsError = e.toAppError(ErrorContext.LOAD_MY_EVENTS)) }
            } finally {
                _uiState.update { it.copy(isLoadingEvents = false) }
            }
        }
    }

    // pull to refresh: profile and events reload together, the screen keeps what it shows meanwhile
    fun refresh() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { it.copy(isRefreshing = true) }
        loadProfile(refresh = true)
        loadEvents()
        val loads = listOfNotNull(loadJob, eventsJob)
        viewModelScope.launch {
            // join also returns when a load is cancelled by a newer one
            try {
                loads.joinAll()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    // a refresh skips the full screen loader and keeps the profile already shown if it fails
    fun loadProfile(refresh: Boolean = false) {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            // a refresh leaves the screen as it is, the error included, until it settles
            if (!refresh) _uiState.update { it.loadingProfile() }
            try {
                val profile = repository.getProfile()
                _uiState.update { it.withProfile(profile) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // ktor hands a cancellation to the response validator, which rethrows it as an
                // ApiException with no status: a load cancelled by a newer one must not write its error
                currentCoroutineContext().ensureActive()
                AppLog.e("Profile", "could not load the profile", e)
                _uiState.update { it.withProfileError(e.toAppError(ErrorContext.LOAD_PROFILE)) }
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
                // on this call a 403 means the current password is wrong
                val error = e.toAppError(ErrorContext.CHANGE_PASSWORD)
                _uiState.update { it.copy(isChangingPassword = false, passwordError = error) }
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
