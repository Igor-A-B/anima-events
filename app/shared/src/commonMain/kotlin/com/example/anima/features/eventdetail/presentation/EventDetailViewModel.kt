package com.example.anima.features.eventdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anima.features.feed.data.MockFeedRepository
import com.example.anima.features.feed.domain.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {

    private val repository = MockFeedRepository()

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    fun loadEvent(id: String) {
        viewModelScope.launch {
            _event.value = repository.findById(id)
        }
    }
}