package com.example.anima.features.addevent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_created
import anima.app.shared.generated.resources.add_event_deleted
import anima.app.shared.generated.resources.add_event_error_date
import anima.app.shared.generated.resources.add_event_error_forbidden
import anima.app.shared.generated.resources.add_event_error_validation
import anima.app.shared.generated.resources.add_event_updated
import anima.app.shared.generated.resources.core_error_image_unsupported
import com.anima.features.event.models.EventCategory
import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.error.Severity
import com.example.anima.core.image.PickedImage
import com.example.anima.core.network.ApiException
import com.example.anima.features.eventdetail.data.EventImageRepository
import com.example.anima.features.addevent.data.ExhibitorEventRepository
import com.example.anima.features.addevent.domain.model.toDraft
import com.example.anima.features.addevent.domain.model.toRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// one form for both creating and editing, load(eventId) switches it to editing
class AddEventViewModel(
    private val repository: ExhibitorEventRepository,
    private val bus: AppExceptionBus,
    private val images: EventImageRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    // called again on recomposition with the same id, only the first call loads
    fun load(eventId: String?) {
        if (eventId == null || _uiState.value.eventId == eventId) return
        _uiState.update { it.copy(eventId = eventId, isLoading = true, loadFailed = false) }

        viewModelScope.launch {
            try {
                val draft = repository.getForm(eventId).toDraft()
                _uiState.update { it.copy(draft = draft, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, loadFailed = true) }
                report(e)
            }
        }
    }

    fun onPhotoPicked(photo: PickedImage) {
        _uiState.update { it.copy(photo = photo) }
    }

    fun onPhotoRemoved() {
        _uiState.update { it.copy(photo = null) }
    }

    fun onUnsupportedPhoto() {
        bus.show(Res.string.core_error_image_unsupported, Severity.ERROR)
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(draft = it.draft.copy(name = name)) }
    }

    fun onDateChanged(date: String) {
        _uiState.update { it.copy(draft = it.draft.copy(date = date)) }
    }

    fun onTimeChanged(time: String) {
        _uiState.update { it.copy(draft = it.draft.copy(time = time)) }
    }

    fun onLocationChanged(location: String) {
        _uiState.update { it.copy(draft = it.draft.copy(location = location)) }
    }

    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(draft = it.draft.copy(address = address)) }
    }

    fun onCityChanged(city: String) {
        _uiState.update { it.copy(draft = it.draft.copy(city = city)) }
    }

    fun onCategorySelected(category: EventCategory) {
        _uiState.update { it.copy(draft = it.draft.copy(category = category)) }
    }

    fun onAboutChanged(about: String) {
        _uiState.update { it.copy(draft = it.draft.copy(about = about)) }
    }

    fun onPriceChanged(price: String) {
        _uiState.update { it.copy(draft = it.draft.copy(price = price)) }
    }

    fun onCapacityChanged(capacity: String) {
        _uiState.update { it.copy(draft = it.draft.copy(capacity = capacity)) }
    }

    fun onSubmit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.canSubmit) return
        val request = state.draft.toRequest()
        if (request == null) {
            bus.show(Res.string.add_event_error_date, Severity.WARN)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val saved = if (state.eventId == null) {
                    repository.create(request).also { bus.show(Res.string.add_event_created, Severity.SUCCESS) }
                } else {
                    repository.update(state.eventId, request).also { bus.show(Res.string.add_event_updated, Severity.SUCCESS) }
                }
                state.photo?.let { uploadPhoto(saved.id, it) }
                // a fresh form for the next event, the create screen stays in the back stack
                _uiState.value = AddEventUiState()
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false) }
                report(e)
            }
        }
    }

    // the event is already saved, a failed upload is only reported
    private suspend fun uploadPhoto(eventId: String, photo: PickedImage) {
        try {
            images.upload(eventId, photo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            report(e)
        }
    }

    fun onDeleteClick() {
        if (_uiState.value.isEditing) _uiState.update { it.copy(isDeleteDialogOpen = true) }
    }

    fun onDismissDeleteDialog() {
        if (!_uiState.value.isDeleting) _uiState.update { it.copy(isDeleteDialogOpen = false) }
    }

    fun onConfirmDelete(onDeleted: () -> Unit) {
        val eventId = _uiState.value.eventId ?: return
        if (_uiState.value.isDeleting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            try {
                repository.delete(eventId)
                _uiState.update { it.copy(isDeleting = false, isDeleteDialogOpen = false) }
                bus.show(Res.string.add_event_deleted, Severity.SUCCESS)
                onDeleted()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isDeleting = false, isDeleteDialogOpen = false) }
                report(e)
            }
        }
    }

    // the generic mapping reads 403 as a wrong password, here it means not allowed
    private fun report(e: Exception) {
        when ((e as? ApiException)?.status) {
            400 -> bus.show(Res.string.add_event_error_validation, Severity.WARN)
            403 -> bus.show(Res.string.add_event_error_forbidden, Severity.ERROR)
            else -> bus.report(e)
        }
    }
}
