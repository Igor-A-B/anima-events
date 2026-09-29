package com.anima.features.event.services

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.dtos.EventPageDto
import com.anima.features.event.entities.EventEntity
import com.anima.features.event.exceptions.EventForbiddenException
import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.EventStatus
import com.anima.features.event.models.FeedSectionType
import com.anima.features.event.repositories.EventRepository
import com.anima.features.subscription.entities.SubscriptionEntity
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.subscription.repositories.SubscriptionRepository
import com.anima.features.user.repositories.UserRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// ~11 km box around the user for the nearby section
private const val NEARBY_DEGREES = 0.1

private val ACTIVE = listOf(SubscriptionStatus.CONFIRMED, SubscriptionStatus.ATTENDED)

@Service
@Transactional
class EventService(
    private val events: EventRepository,
    private val users: UserRepository,
    private val subscriptions: SubscriptionRepository,
) {
    fun create(userId: UUID, request: CreateEventRequestDto): Event {
        val organizer = users.findById(userId).orElseThrow { EventForbiddenException() }
        val entity = EventEntity().apply { this.organizer = organizer }
        return events.save(entity.fill(request)).let { it.toEvent(attendeesOf(it)) }
    }

    @Transactional(readOnly = true)
    fun get(id: UUID): Event = find(id).let { it.toEvent(attendeesOf(it)) }

    fun update(userId: UUID, id: UUID, request: CreateEventRequestDto): Event {
        val entity = findOwned(userId, id)
        return events.save(entity.fill(request)).let { it.toEvent(attendeesOf(it)) }
    }

    fun delete(userId: UUID, id: UUID) {
        events.deleteById(findOwned(userId, id).id!!)
    }

    // every filter is optional; section decides the base rule and the order
    @Transactional(readOnly = true)
    fun search(
        section: FeedSectionType?,
        category: EventCategory?,
        query: String?,
        lat: Double?,
        lng: Double?,
        userId: UUID?,
        page: Int,
        size: Int,
    ): EventPageDto {
        val now = LocalDateTime.now()
        val liveStart = now.minusHours(LIVE_HOURS)
        val specs = mutableListOf<Specification<EventEntity>>()
        var sort = Sort.by("startsAt")

        when (section) {
            FeedSectionType.HAPPENING_NOW -> specs += Specification { r, _, cb ->
                cb.and(cb.lessThanOrEqualTo(r.get("startsAt"), now), cb.greaterThan(r.get("startsAt"), liveStart))
            }
            FeedSectionType.NEARBY -> {
                require(lat != null && lng != null) { "lat and lng are required for NEARBY" }
                specs += Specification { r, _, cb ->
                    cb.and(
                        cb.greaterThan(r.get("startsAt"), liveStart),
                        cb.between(r.get("latitude"), lat - NEARBY_DEGREES, lat + NEARBY_DEGREES),
                        cb.between(r.get("longitude"), lng - NEARBY_DEGREES, lng + NEARBY_DEGREES),
                    )
                }
            }
            FeedSectionType.RECOMMENDED -> {
                // TODO: rank by popularity once there is enough data
                specs += Specification { r, _, cb -> cb.greaterThan(r.get("startsAt"), liveStart) }
            }
            FeedSectionType.PARTICIPATING -> {
                requireNotNull(userId) { "login is required for PARTICIPATING" }
                // events the caller has a CONFIRMED subscription for
                specs += Specification { r, q, cb ->
                    val sub = q!!.subquery(UUID::class.java)
                    val s = sub.from(SubscriptionEntity::class.java)
                    sub.select(s.get<EventEntity>("event").get<UUID>("id")).where(
                        cb.equal(s.get<Any>("visitor").get<Any>("user").get<UUID>("id"), userId),
                        cb.equal(s.get<SubscriptionStatus>("status"), SubscriptionStatus.CONFIRMED),
                    )
                    r.get<UUID>("id").`in`(sub)
                }
            }
            null -> Unit
        }
        if (category != null) specs += Specification { r, _, cb -> cb.equal(r.get<EventCategory>("category"), category) }
        if (!query.isNullOrBlank()) {
            val like = "%${query.trim().lowercase()}%"
            specs += Specification { r, _, cb ->
                cb.or(cb.like(cb.lower(r.get("title")), like), cb.like(cb.lower(r.get("venue")), like)) as Predicate
            }
        }

        val result = events.search(Specification.allOf(specs), PageRequest.of(page.coerceAtLeast(0), size.coerceIn(1, 50), sort))
        return EventPageDto(result.content.map { it.toEvent(attendeesOf(it), lat, lng) }, result.number, result.size, result.totalElements)
    }

    private fun attendeesOf(event: EventEntity) =
        subscriptions.countByEventIdAndStatusIn(event.id!!, ACTIVE).toInt()

    private fun find(id: UUID) = events.findById(id).orElseThrow { EventNotFoundException() }

    private fun findOwned(userId: UUID, id: UUID): EventEntity {
        val entity = find(id)
        if (entity.organizer?.id != userId) throw EventForbiddenException()
        return entity
    }

    private fun EventEntity.fill(r: CreateEventRequestDto): EventEntity {
        require(r.title.isNotBlank() && r.venue.isNotBlank() && r.city.isNotBlank()) { "title, venue and city are required" }
        title = r.title.trim()
        category = r.category
        venue = r.venue.trim()
        city = r.city.trim()
        startsAt = try {
            LocalDateTime.parse(r.startsAt)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("startsAt must be like 2026-10-05T20:00:00")
        }
        description = r.description
        price = r.price
        capacity = r.capacity
        latitude = r.latitude
        longitude = r.longitude
        return this
    }
}
