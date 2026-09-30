package com.anima.features.event.services

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.dtos.EventPageDto
import com.anima.features.event.dtos.UpdateEventRequestDto
import com.anima.features.event.entities.EventEntity
import com.anima.features.event.exceptions.EventForbiddenException
import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.event.exceptions.ExhibitorOnlyException
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.EventStatus
import com.anima.features.event.models.FeedSectionType
import com.anima.features.event.repositories.EventRepository
import com.anima.features.exhibitor.repositories.ExhibitorRepository
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
    private val exhibitors: ExhibitorRepository,
) {
    // only exhibitors organize events, visitors just subscribe to them
    fun create(userId: UUID, request: CreateEventRequestDto): Event {
        if (!exhibitors.existsByUserId(userId)) throw ExhibitorOnlyException()
        val organizer = users.findById(userId).orElseThrow { ExhibitorOnlyException() }
        val entity = EventEntity().apply { this.organizer = organizer }
        return events.save(entity.fill(request)).let { it.toEvent(attendeesOf(it)) }
    }

    @Transactional(readOnly = true)
    fun get(id: UUID): Event = find(id).let { it.toEvent(attendeesOf(it)) }

    // the organizer's own events, newest first, past ones included
    @Transactional(readOnly = true)
    fun mine(userId: UUID): List<Event> =
        events.findAllByOrganizerId(userId).map { it.toEvent(attendeesOf(it)) }

    // raw stored values for the edit form, Event only has display labels
    @Transactional(readOnly = true)
    fun form(userId: UUID, id: UUID): UpdateEventRequestDto = findOwned(userId, id).let {
        UpdateEventRequestDto(
            title = it.title,
            category = it.category,
            venue = it.venue,
            city = it.city,
            startsAt = it.startsAt.toString(),
            description = it.description,
            price = it.price,
            capacity = it.capacity,
            latitude = it.latitude,
            longitude = it.longitude,
            address = it.address,
            imageUrl = it.imageUrl,
        )
    }

    fun update(userId: UUID, id: UUID, request: CreateEventRequestDto): Event {
        val entity = findOwned(userId, id)
        return events.save(entity.fill(request)).let { it.toEvent(attendeesOf(it)) }
    }

    // subscriptions point at the event, they go first or the foreign key blocks the delete
    fun delete(userId: UUID, id: UUID) {
        val eventId = findOwned(userId, id).id!!
        subscriptions.deleteAllByEventId(eventId)
        events.deleteById(eventId)
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
        cursor: String?,
        size: Int,
    ): EventPageDto {
        val now = LocalDateTime.now()
        val liveStart = now.minusHours(LIVE_HOURS)
        val specs = mutableListOf<Specification<EventEntity>>()
        val sort = Sort.by("startsAt", "id")

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

        // keyset pagination: continue right after the last event of the previous page
        if (!cursor.isNullOrBlank()) {
            val after = EventCursor.decode(cursor)
            specs += Specification { r, _, cb ->
                cb.or(
                    cb.greaterThan(r.get("startsAt"), after.startsAt),
                    cb.and(cb.equal(r.get<LocalDateTime>("startsAt"), after.startsAt), cb.greaterThan(r.get("id"), after.id)),
                )
            }
        }

        // one extra row tells whether there is a next page
        val limit = size.coerceIn(1, 50)
        val rows = events.search(Specification.allOf(specs), PageRequest.of(0, limit + 1, sort)).content
        val page = rows.take(limit)
        val nextCursor = if (rows.size > limit) page.last().let { EventCursor(it.startsAt, it.id!!).encode() } else null
        return EventPageDto(page.map { it.toEvent(attendeesOf(it), lat, lng) }, nextCursor)
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
        require((r.price ?: 0.0) >= 0) { "price can't be negative" }
        require((r.capacity ?: 1) > 0) { "capacity must be positive" }
        description = r.description.trim()
        price = r.price
        capacity = r.capacity
        latitude = r.latitude
        longitude = r.longitude
        address = r.address?.trim()?.ifBlank { null }
        imageUrl = r.imageUrl?.trim()?.ifBlank { null }
        return this
    }
}
