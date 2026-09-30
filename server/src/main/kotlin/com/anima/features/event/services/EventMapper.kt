package com.anima.features.event.services

import com.anima.features.event.entities.EventEntity
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventStatus
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// how long an event counts as "happening now" after it starts
internal const val LIVE_HOURS = 3L

// dateLabel/timeLabel/status/distance are display values, computed here instead of stored
// publicUrl turns a storage path into a url the app can load (StorageService::publicUrl)
internal fun EventEntity.toEvent(
    attendees: Int,
    publicUrl: (String) -> String,
    lat: Double? = null,
    lng: Double? = null,
): Event {
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
        address = address,
        imageUrl = imageUrl,
        curatorId = organizer?.id?.toString() ?: "",
        imageUrls = images.map { publicUrl(it.objectPath) },
    )
}

private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return 2 * 6371.0 * asin(sqrt(a))
}
