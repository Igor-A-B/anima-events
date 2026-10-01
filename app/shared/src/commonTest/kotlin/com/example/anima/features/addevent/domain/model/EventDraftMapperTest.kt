package com.example.anima.features.addevent.domain.model

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.models.EventCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EventDraftMapperTest {

    private val draft = EventDraft(
        name = " Noite de Jazz ",
        date = "05/10/2026",
        time = "20:30",
        location = "Galpao 9",
        address = "Rua Augusta, 900",
        city = "Sao Paulo",
        category = EventCategory.MUSIC,
        price = "25,50",
        capacity = "120",
    )

    @Test
    fun picker_format_becomes_iso() {
        assertEquals("2026-10-05T20:30:00", draft.startsAtIso())
    }

    @Test
    fun iso_date_is_accepted_too() {
        assertEquals("2026-10-05T09:05:00", draft.copy(date = "2026-10-05", time = "9:05").startsAtIso())
    }

    @Test
    fun invalid_date_or_time_gives_null() {
        assertNull(draft.copy(date = "5/13/2026").startsAtIso())
        assertNull(draft.copy(time = "25:00").startsAtIso())
        assertNull(draft.copy(date = "amanha").toRequest())
    }

    @Test
    fun request_trims_and_parses_numbers() {
        val request = draft.toRequest()!!
        assertEquals("Noite de Jazz", request.title)
        assertEquals(25.5, request.price)
        assertEquals(120, request.capacity)
        assertEquals("Rua Augusta, 900", request.address)
        assertNull(draft.copy(price = "", address = " ").toRequest()!!.let { it.price ?: it.address })
    }

    @Test
    fun stored_values_round_trip_through_the_draft() {
        val stored = CreateEventRequestDto(
            title = "Noite de Jazz",
            category = EventCategory.MUSIC,
            venue = "Galpao 9",
            city = "Sao Paulo",
            // LocalDateTime.toString() drops zero seconds
            startsAt = "2026-10-05T20:30",
            price = 30.0,
            capacity = 120,
            latitude = -23.55,
            longitude = -46.63,
        )
        val draft = stored.toDraft()
        assertEquals("05/10/2026", draft.date)
        assertEquals("20:30", draft.time)
        assertEquals("30", draft.price)
        assertEquals(stored.copy(startsAt = "2026-10-05T20:30:00"), draft.toRequest())
    }
}
