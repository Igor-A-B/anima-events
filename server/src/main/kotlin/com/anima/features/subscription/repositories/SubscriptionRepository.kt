package com.anima.features.subscription.repositories

import com.anima.features.subscription.entities.SubscriptionEntity
import com.anima.features.subscription.models.SubscriptionStatus
import java.util.Optional
import java.util.UUID

interface SubscriptionRepository {
    fun findByVisitorIdAndEventId(visitorId: String, eventId: UUID): Optional<SubscriptionEntity>
    fun findAllByVisitorId(visitorId: String): List<SubscriptionEntity>
    fun countByEventIdAndStatusIn(eventId: UUID, statuses: Collection<SubscriptionStatus>): Long
    fun save(subscription: SubscriptionEntity): SubscriptionEntity
    fun deleteAllByEventId(eventId: UUID)
}
