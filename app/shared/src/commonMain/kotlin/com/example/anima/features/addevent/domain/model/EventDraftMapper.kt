package com.example.anima.features.addevent.domain.model

import com.anima.features.event.dtos.CreateEventRequestDto

// the form's dd/MM/yyyy + HH:mm as the ISO local date time the server takes, null when either is invalid
// yyyy-MM-dd is accepted too, the server still rejects impossible days like 31/02
fun EventDraft.startsAtIso(): String? {
    val dateParts = date.trim().split('/', '-').mapNotNull { it.toIntOrNull() }
    if (dateParts.size != 3) return null
    val (day, month, year) = if (date.contains('-')) dateParts.reversed() else dateParts
    val timeParts = time.trim().split(':').mapNotNull { it.toIntOrNull() }
    if (timeParts.size != 2) return null
    val (hour, minute) = timeParts

    if (year !in 1000..9999 || month !in 1..12 || day !in 1..31 || hour !in 0..23 || minute !in 0..59) return null
    return "${year.pad(4)}-${month.pad()}-${day.pad()}T${hour.pad()}:${minute.pad()}:00"
}

// null when the date or time can't be read, the other fields are checked by the server
fun EventDraft.toRequest(): CreateEventRequestDto? {
    val startsAt = startsAtIso() ?: return null
    return CreateEventRequestDto(
        title = name.trim(),
        category = category ?: return null,
        venue = location.trim(),
        city = city.trim(),
        startsAt = startsAt,
        description = about.trim(),
        // "25,50" and "25.50" both work, empty means free
        price = price.trim().replace(',', '.').toDoubleOrNull(),
        capacity = capacity.trim().toIntOrNull(),
        latitude = latitude,
        longitude = longitude,
        address = address.trim().ifBlank { null },
        imageUrl = photoUri,
    )
}

// back from the stored values, startsAt is like 2026-10-05T20:00 or 2026-10-05T20:00:00
fun CreateEventRequestDto.toDraft(): EventDraft {
    val (datePart, timePart) = startsAt.split('T', limit = 2).let { it[0] to it.getOrElse(1) { "" } }
    val (year, month, day) = datePart.split('-').let { Triple(it[0], it.getOrElse(1) { "" }, it.getOrElse(2) { "" }) }
    return EventDraft(
        photoUri = imageUrl,
        name = title,
        date = "$day/$month/$year",
        time = timePart.take(5),
        location = venue,
        address = address.orEmpty(),
        city = city,
        category = category,
        about = description,
        price = price?.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() }.orEmpty(),
        capacity = capacity?.toString().orEmpty(),
        latitude = latitude,
        longitude = longitude,
    )
}

private fun Int.pad(length: Int = 2) = toString().padStart(length, '0')
