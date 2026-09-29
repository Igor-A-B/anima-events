package com.example.anima.core.error

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

// one-off things a screen shows, not part of the ui state
sealed interface UiEvent {
    data class ShowError(val error: AppError) : UiEvent
}

// TODO(snackbar): collect events in a SnackbarHost, nothing does it yet
class UiEventEmitter {
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = channel.receiveAsFlow()

    fun emit(event: UiEvent) {
        channel.trySend(event)
    }
}
