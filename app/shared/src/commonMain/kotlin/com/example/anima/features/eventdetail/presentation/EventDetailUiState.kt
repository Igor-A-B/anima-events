package com.example.anima.features.eventdetail.presentation

import com.anima.features.event.models.Event
import com.example.anima.core.error.AppError
import com.anima.features.event.models.EventStatus
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.user.models.AccountType

// what the footer button shows
enum class SubscriptionUi {
    // exhibitors, and finished events the visitor was not part of
    Hidden,
    NotSubscribed,
    Loading,
    Confirmed,
    Cancelled,
    Attended,
    // no spot left for a visitor that is not subscribed, the button is disabled
    Full,
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

// null when the event has no limit
fun Event.spotsLeft(): Int? = capacity?.let { (it - attendees).coerceAtLeast(0) }

fun Event.isFull(): Boolean = capacity?.let { attendees >= it } ?: false

// the footer state; a visitor already going keeps Confirmed on a full event so they can cancel
fun subscriptionUiFor(
    event: Event?,
    subscriptionStatus: SubscriptionStatus?,
    accountType: AccountType?,
    busy: Boolean,
): SubscriptionUi {
    if (event == null || accountType != AccountType.VISITOR) return SubscriptionUi.Hidden
    if (busy) return SubscriptionUi.Loading
    val finished = event.status == EventStatus.FINISHED
    return when (subscriptionStatus) {
        SubscriptionStatus.CONFIRMED -> SubscriptionUi.Confirmed
        SubscriptionStatus.ATTENDED -> SubscriptionUi.Attended
        SubscriptionStatus.CANCELLED, null -> when {
            finished -> SubscriptionUi.Hidden
            event.isFull() -> SubscriptionUi.Full
            subscriptionStatus == SubscriptionStatus.CANCELLED -> SubscriptionUi.Cancelled
            else -> SubscriptionUi.NotSubscribed
        }
    }
}
