package com.example.anima.features.eventdetail.presentation

import com.anima.features.event.models.Event
import com.example.anima.core.error.AppError

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
    // why the last subscribe or cancel failed
    val actionError: AppError? = null,
    // the signed in user organizes this event, so they can add images
    val isCurator: Boolean = false,
    val isUploadingImage: Boolean = false,
)
