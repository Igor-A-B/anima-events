package com.example.anima.features.subscription.data

import com.anima.features.subscription.models.Subscription
import kotlinx.coroutines.flow.StateFlow

// the visitor's subscriptions, observable because the feed and the detail screen both react to them
interface SubscriptionRepository {
    val subscriptions: StateFlow<List<Subscription>>

    suspend fun refresh()

    suspend fun subscribe(eventId: String)

    suspend fun cancel(eventId: String)
}
