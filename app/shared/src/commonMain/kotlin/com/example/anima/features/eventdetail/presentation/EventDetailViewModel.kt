package com.example.anima.features.eventdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventStatus
import com.anima.features.subscription.models.SubscriptionStatus
import com.example.anima.features.auth.data.SessionRepository
import com.anima.features.user.models.AccountType
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.subscription.data.SubscriptionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val feedRepository: FeedRepository,
    private val subscriptions: SubscriptionRepository,
    session: SessionRepository,
) : ViewModel() {

    private val event = MutableStateFlow<Event?>(null)
    private val busy = MutableStateFlow(false)
    private val actionError = MutableStateFlow(false)

    val uiState: StateFlow<EventDetailUiState> = combine(
        event, subscriptions.subscriptions, session.session, busy, actionError,
    ) { event, subs, session, busy, actionError ->
        val state = when {
            event == null || session?.accountType != AccountType.VISITOR -> SubscriptionUi.Hidden
            busy -> SubscriptionUi.Loading
            else -> {
                val finished = event.status == EventStatus.FINISHED
                when (subs.firstOrNull { it.event.id == event.id }?.status) {
                    SubscriptionStatus.CONFIRMED -> SubscriptionUi.Confirmed
                    SubscriptionStatus.ATTENDED -> SubscriptionUi.Attended
                    SubscriptionStatus.CANCELLED -> if (finished) SubscriptionUi.Hidden else SubscriptionUi.Cancelled
                    null -> if (finished) SubscriptionUi.Hidden else SubscriptionUi.NotSubscribed
                }
            }
        }
        EventDetailUiState(event, state, actionError)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventDetailUiState())

    fun loadEvent(id: String) {
        viewModelScope.launch {
            event.value = runCatching { feedRepository.findById(id) }.getOrNull()
            runCatching { subscriptions.refresh() }
        }
    }

    fun onSubscribeClick() {
        val current = event.value ?: return
        val subscribe = when (uiState.value.subscription) {
            SubscriptionUi.NotSubscribed, SubscriptionUi.Cancelled -> true
            SubscriptionUi.Confirmed -> false
            else -> return
        }

        viewModelScope.launch {
            busy.value = true
            actionError.value = false
            try {
                if (subscribe) subscriptions.subscribe(current.id) else subscriptions.cancel(current.id)
                // the attendees count changed on the server
                runCatching { feedRepository.findById(current.id) }.getOrNull()?.let { event.value = it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                actionError.value = true
            } finally {
                busy.value = false
            }
        }
    }
}
