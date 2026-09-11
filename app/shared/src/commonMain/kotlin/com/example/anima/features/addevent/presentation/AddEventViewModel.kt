package com.example.anima.features.addevent.presentation

import androidx.lifecycle.ViewModel
import com.example.anima.features.feed.domain.EventCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddEventViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    fun onPhotoSelected(uri: String) {
        _uiState.update { it.copy(draft = it.draft.copy(photoUri = uri)) }
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
        // when backend ready, call usecase here
        onSuccess()
    }
}