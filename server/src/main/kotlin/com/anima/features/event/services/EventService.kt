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

// how long an event counts as "happening now" after it starts
private const val LIVE_HOURS = 3L
// ~11 km box around the user for the nearby section
private const val NEARBY_DEGREES = 0.1

@Service
@Transactional
class EventService(
    private val events: EventRepository,
    private val users: UserRepository,
) {
    fun create(userId: UUID, request: CreateEventRequestDto): Event {
        val organizer = users.findById(userId).orElseThrow { EventForbiddenException() }
        val entity = EventEntity().apply { this.organizer = organizer }
        return events.save(entity.fill(request)).toEvent()
    }

    @Transactional(readOnly = true)
    fun get(id: UUID): Event = find(id).toEvent()

    fun update(userId: UUID, id: UUID, request: CreateEventRequestDto): Event {
        val entity = findOwned(userId, id)
        return events.save(entity.fill(request)).toEvent()
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
                specs += Specification { r, _, cb -> cb.greaterThan(r.get("startsAt"), liveStart) }
                sort = Sort.by(Sort.Order.desc("attendees"), Sort.Order.asc("startsAt"))
            }
            // TODO: swap for an attendance table when tickets exist
            FeedSectionType.PARTICIPATING -> {
                requireNotNull(userId) { "login is required for PARTICIPATING" }
                specs += Specification { r, _, cb -> cb.equal(r.get<Any>("organizer").get<UUID>("id"), userId) }
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
        return EventPageDto(result.content.map { it.toEvent(lat, lng) }, result.number, result.size, result.totalElements)
    }

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

// dateLabel/timeLabel/status/distance are display values, computed here instead of stored
private fun EventEntity.toEvent(lat: Double? = null, lng: Double? = null): Event {
    val now = LocalDateTime.now()
    val status = when {
        startsAt.isAfter(now) -> EventStatus.UPCOMING
        startsAt.isAfter(now.minusHours(LIVE_HOURS)) -> EventStatus.OCCURRING
        else -> EventStatus.FINISHED
    }
    val days = java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(), startsAt.toLocalDate())
    val dateLabel = when {
        status == EventStatus.OCCURRING -> "Agora"
        days == 0L -> "Hoje"
        days == 1L -> "Amanhã"
        else -> "%02d/%02d".format(startsAt.dayOfMonth, startsAt.monthValue)
    }
    val km = if (lat != null && lng != null && latitude != null && longitude != null) {
        haversineKm(lat, lng, latitude!!, longitude!!)
    } else null
    return Event(
        id = id.toString(),
        title = title,
        category = category,
        venue = venue,
        city = city,
        dateLabel = dateLabel,
        timeLabel = "%02d:%02d".format(startsAt.hour, startsAt.minute),
        price = price?.takeIf { it > 0 }?.let { String.format(Locale.US, "R$ %.2f", it) },
        distanceLabel = km?.let { if (it < 1) "${(it * 1000).toInt()} m" else String.format(Locale.US, "%.1f km", it) },
        attendees = attendees,
        status = status,
        coverSeed = abs(id.hashCode()),
        description = description,
        organizerName = organizer?.name ?: "",
    )
}

private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return 2 * 6371.0 * asin(sqrt(a))
}
