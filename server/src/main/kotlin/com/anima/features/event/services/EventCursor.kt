package com.anima.features.event.services

import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID

// keyset position of the last event of a page, sent to the client as an opaque string
internal data class EventCursor(val startsAt: LocalDateTime, val id: UUID) {
    fun encode(): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString("$startsAt|$id".toByteArray())

    companion object {
        fun decode(raw: String): EventCursor = try {
            val (startsAt, id) = String(Base64.getUrlDecoder().decode(raw)).split("|", limit = 2)
            EventCursor(LocalDateTime.parse(startsAt), UUID.fromString(id))
        } catch (e: Exception) {
            throw IllegalArgumentException("invalid cursor")
        }
    }
}
