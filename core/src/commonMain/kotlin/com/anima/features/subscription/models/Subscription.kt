package com.anima.features.subscription.models

import com.anima.features.event.models.Event
import kotlinx.serialization.Serializable

enum class SubscriptionStatus {
    CANCELLED,
    CONFIRMED,
    ATTENDED,
}

// createdTimestamp is epoch millis
@Serializable
data class Subscription(
    val id: String,
    val visitorId: String,
    val event: Event,
    val createdTimestamp: Long,
    val status: SubscriptionStatus,
)
