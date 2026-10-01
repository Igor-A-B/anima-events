package com.example.anima.core.events

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// counts writes to events (create, update, delete, image upload) so the feed and search,
// which only load when they are created, know to fetch again
class EventChanges {

    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version.asStateFlow()

    fun notifyChanged() {
        _version.update { it + 1 }
    }
}
