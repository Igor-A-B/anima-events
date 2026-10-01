package com.example.anima.features.addevent.presentation

import com.example.anima.core.error.AppError
import com.example.anima.core.image.PickedImage
import com.example.anima.features.addevent.domain.model.EventDraft

data class AddEventUiState(
    val draft: EventDraft = EventDraft(),
    // picked or taken, uploaded once the event is saved
    val photo: PickedImage? = null,
    val existingPhotoUrl: String? = null,
    // null when creating, the event being edited otherwise
    val eventId: String? = null,
    // loading the stored values of the event being edited
    val isLoading: Boolean = false,
    // why loading the event being edited failed
    val loadError: AppError? = null,
    val isSubmitting: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val isDeleting: Boolean = false,
) {
    val isEditing: Boolean = eventId != null
    val loadFailed: Boolean = loadError != null

    val canSubmit: Boolean =
        !isLoading && !loadFailed && !isSubmitting && !isDeleting &&
                draft.name.isNotBlank() &&
                draft.date.isNotBlank() &&
                draft.time.isNotBlank() &&
                draft.location.isNotBlank() &&
                draft.city.isNotBlank() &&
                draft.category != null
}
