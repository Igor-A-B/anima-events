package com.anima.features.subscription.repositories

import com.anima.features.subscription.entities.SubscriptionEntity
import com.anima.features.subscription.models.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class SubscriptionRepositoryImpl(private val jpa: SubscriptionJpaRepository) : SubscriptionRepository {
    override fun findByVisitorIdAndEventId(visitorId: String, eventId: UUID): Optional<SubscriptionEntity> =
        jpa.findByVisitorIdAndEventId(visitorId, eventId)
    override fun findAllByVisitorId(visitorId: String): List<SubscriptionEntity> = jpa.findAllByVisitorId(visitorId)
    override fun countByEventIdAndStatusIn(eventId: UUID, statuses: Collection<SubscriptionStatus>): Long =
        jpa.countByEventIdAndStatusIn(eventId, statuses)
    override fun save(subscription: SubscriptionEntity): SubscriptionEntity = jpa.save(subscription)
    override fun deleteAllByEventId(eventId: UUID) = jpa.deleteAllByEventId(eventId)
}

interface SubscriptionJpaRepository : JpaRepository<SubscriptionEntity, UUID> {
    fun findByVisitorIdAndEventId(visitorId: String, eventId: UUID): Optional<SubscriptionEntity>
    fun findAllByVisitorId(visitorId: String): List<SubscriptionEntity>
    fun countByEventIdAndStatusIn(eventId: UUID, statuses: Collection<SubscriptionStatus>): Long
    fun deleteAllByEventId(eventId: UUID)
}
