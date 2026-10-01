package com.example.anima.features.profile.presentation

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventStatus
import com.anima.features.subscription.models.Subscription
import com.anima.features.subscription.models.SubscriptionStatus

// events the visitor takes part in: live first, then upcoming, then ended,
// newest subscription first inside each group. cancelled subscriptions are dropped
fun participatingEvents(subscriptions: List<Subscription>): List<Event> =
    subscriptions
        .filter { it.status != SubscriptionStatus.CANCELLED }
        .sortedByDescending { it.createdTimestamp }
        .sortedBy { it.event.status.groupOrder() } // stable, keeps the newest first inside a group
        .map { it.event }

private fun EventStatus.groupOrder(): Int = when (this) {
    EventStatus.OCCURRING -> 0
    EventStatus.UPCOMING -> 1
    EventStatus.FINISHED -> 2
}
