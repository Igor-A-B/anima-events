package com.anima.testing

import com.anima.features.event.entities.EventEntity
import com.anima.features.event.repositories.EventRepository
import com.anima.features.storage.services.StorageService
import com.anima.features.subscription.entities.SubscriptionEntity
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.subscription.repositories.SubscriptionRepository
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.features.visitor.repositories.VisitorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.util.Optional
import java.util.UUID

// in memory repositories shared by the event and subscription tests

class FakeEventRepository(vararg events: EventEntity) : EventRepository {
    val rows = events.toMutableList()
    val lockedIds = mutableListOf<UUID>()

    override fun findById(id: UUID): Optional<EventEntity> = Optional.ofNullable(rows.find { it.id == id })
    override fun findByIdForUpdate(id: UUID): Optional<EventEntity> {
        lockedIds += id
        return findById(id)
    }
    override fun search(spec: Specification<EventEntity>, pageable: Pageable): Page<EventEntity> = throw NotImplementedError()
    override fun save(event: EventEntity) = event
    override fun findAllByOrganizerId(organizerId: UUID) = rows.filter { it.organizer?.id == organizerId }
    override fun deleteById(id: UUID) { rows.removeIf { it.id == id } }
}

open class FakeSubscriptionRepository : SubscriptionRepository {
    val rows = mutableListOf<SubscriptionEntity>()
    var saves = 0

    override fun findByVisitorIdAndEventId(visitorId: String, eventId: UUID): Optional<SubscriptionEntity> =
        Optional.ofNullable(rows.find { it.visitor?.id == visitorId && it.event?.id == eventId })
    override fun findAllByVisitorId(visitorId: String) = rows.filter { it.visitor?.id == visitorId }
    override fun countByEventIdAndStatusIn(eventId: UUID, statuses: Collection<SubscriptionStatus>): Long =
        rows.count { it.event?.id == eventId && it.status in statuses }.toLong()
    override fun save(subscription: SubscriptionEntity): SubscriptionEntity {
        onSave(subscription)
        saves++
        if (subscription.id == null) {
            subscription.id = UUID.randomUUID()
            rows += subscription
        }
        return subscription
    }
    override fun deleteAllByEventId(eventId: UUID) { rows.removeIf { it.event?.id == eventId } }

    // hook for tests that simulate a concurrent writer
    protected open fun onSave(subscription: SubscriptionEntity) = Unit

    fun add(visitor: VisitorEntity, event: EventEntity, status: SubscriptionStatus) = SubscriptionEntity().also {
        it.id = UUID.randomUUID()
        it.visitor = visitor
        it.event = event
        it.status = status
        rows += it
    }
}

class FakeVisitorRepository(vararg visitors: VisitorEntity) : VisitorRepository {
    private val rows = visitors.toList()
    override fun findById(id: String): Optional<VisitorEntity> = Optional.ofNullable(rows.find { it.id == id })
    override fun findByUserId(userId: UUID): Optional<VisitorEntity> = Optional.ofNullable(rows.find { it.user?.id == userId })
    override fun existsByUserId(userId: UUID) = rows.any { it.user?.id == userId }
    override fun save(visitor: VisitorEntity) = visitor
    override fun deleteById(id: String) = Unit
}

class FakeStorageService : StorageService {
    override fun upload(path: String, bytes: ByteArray, contentType: String) = Unit
    override fun delete(path: String) = Unit
    override fun publicUrl(path: String) = "http://x/$path"
}
