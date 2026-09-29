package com.example.anima.features.eventdetail.presentation

import com.anima.features.event.models.Event

// what the footer button shows
enum class SubscriptionUi {
    // exhibitors, and finished events the visitor was not part of
    Hidden,
    NotSubscribed,
    Loading,
    Confirmed,
    Cancelled,
    Attended,
}

data class EventDetailUiState(
    val event: Event? = null,
    val subscription: SubscriptionUi = SubscriptionUi.Hidden,
    val actionError: Boolean = false,
)
