package com.anima.features.subscription.services

import com.anima.features.event.entities.EventEntity
import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.event.models.EventStatus
import com.anima.features.event.repositories.EventRepository
import com.anima.features.event.services.toEvent
import com.anima.features.storage.services.StorageService
import com.anima.features.subscription.entities.SubscriptionEntity
import com.anima.features.subscription.exceptions.EventAlreadyFinishedException
import com.anima.features.subscription.exceptions.EventFullException
import com.anima.features.subscription.exceptions.NotAVisitorException
import com.anima.features.subscription.exceptions.SubscriptionNotCancellableException
import com.anima.features.subscription.models.Subscription
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.subscription.repositories.SubscriptionRepository
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.features.visitor.repositories.VisitorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

private val ACTIVE = listOf(SubscriptionStatus.CONFIRMED, SubscriptionStatus.ATTENDED)

@Service
@Transactional
class SubscriptionService(
    private val subscriptions: SubscriptionRepository,
    private val visitors: VisitorRepository,
    private val events: EventRepository,
    private val storage: StorageService,
) {
    // idempotent, a cancelled subscription is reused
    // the event row is locked first, so two visitors can't both take the last spot
    fun subscribe(userId: UUID, eventId: UUID): Subscription {
        val visitor = visitorOf(userId)
        val event = events.findByIdForUpdate(eventId).orElseThrow { EventNotFoundException() }
        val existing = subscriptions.findByVisitorIdAndEventId(visitor.id, eventId).orElse(null)
        if (existing != null && existing.status in ACTIVE) return existing.toModel()

        if (event.toEvent(0, storage::publicUrl).status == EventStatus.FINISHED) throw EventAlreadyFinishedException()
        // a cancelled subscription coming back takes a spot like a new one
        val capacity = event.capacity
        if (capacity != null && subscriptions.countByEventIdAndStatusIn(eventId, ACTIVE) >= capacity) {
            throw EventFullException()
        }
        val entity = existing ?: SubscriptionEntity().also {
            it.visitor = visitor
            it.event = event
        }
        entity.status = SubscriptionStatus.CONFIRMED
        entity.createdTimestamp = Instant.now()
        return subscriptions.save(entity).toModel()
    }

    fun cancel(userId: UUID, eventId: UUID): Subscription {
        val visitor = visitorOf(userId)
        val entity = subscriptions.findByVisitorIdAndEventId(visitor.id, eventId).orElseThrow { EventNotFoundException() }
        if (entity.status == SubscriptionStatus.ATTENDED) throw SubscriptionNotCancellableException()
        entity.status = SubscriptionStatus.CANCELLED
        return subscriptions.save(entity).toModel()
    }

    @Transactional(readOnly = true)
    fun listMine(userId: UUID): List<Subscription> =
        subscriptions.findAllByVisitorId(visitorOf(userId).id).map { it.toModel() }

    private fun visitorOf(userId: UUID): VisitorEntity =
        visitors.findByUserId(userId).orElseThrow { NotAVisitorException() }

    private fun SubscriptionEntity.toModel(): Subscription {
        val event: EventEntity = event!!
        return Subscription(
            id = id.toString(),
            visitorId = visitor!!.id,
            event = event.toEvent(subscriptions.countByEventIdAndStatusIn(event.id!!, ACTIVE).toInt(), storage::publicUrl),
            createdTimestamp = createdTimestamp.toEpochMilli(),
            status = status,
        )
    }
}
