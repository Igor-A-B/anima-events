package com.example.anima.features.addevent.presentation

import com.example.anima.features.addevent.domain.model.EventDraft

data class AddEventUiState(
    val draft: EventDraft = EventDraft(),
    val isLoading: Boolean = false,
    val error: String = "",
) {
    val canSubmit: Boolean =
        draft.name.isNotBlank() &&
                draft.date.isNotBlank() &&
                draft.time.isNotBlank() &&
                draft.location.isNotBlank() &&
                draft.category != null
}