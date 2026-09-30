package com.anima.features.event.services

import com.anima.features.event.models.DateFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventSearchFiltersTest {
    // a wednesday
    private val wednesday = LocalDateTime.of(2026, 9, 30, 21, 0)

    @Test
    fun `any has no bounds`() {
        assertEquals(StartsAtRange(null, null), DateFilter.ANY.startsAtRange(wednesday))
    }

    @Test
    fun `today keeps live events and ends at midnight`() {
        val range = DateFilter.TODAY.startsAtRange(wednesday)
        assertEquals(wednesday.minusHours(LIVE_HOURS), range.from)
        assertEquals(LocalDateTime.of(2026, 10, 1, 0, 0), range.until)
    }

    @Test
    fun `tomorrow is the whole next day`() {
        val range = DateFilter.TOMORROW.startsAtRange(wednesday)
        assertEquals(StartsAtRange(LocalDateTime.of(2026, 10, 1, 0, 0), LocalDateTime.of(2026, 10, 2, 0, 0)), range)
    }

    @Test
    fun `weekend on a weekday is the coming saturday and sunday`() {
        val range = DateFilter.WEEKEND.startsAtRange(wednesday)
        assertEquals(StartsAtRange(LocalDateTime.of(2026, 10, 3, 0, 0), LocalDateTime.of(2026, 10, 5, 0, 0)), range)
    }

    @Test
    fun `weekend on a sunday starts with the live window and ends on monday`() {
        val sunday = LocalDateTime.of(2026, 10, 4, 15, 0)
        val range = DateFilter.WEEKEND.startsAtRange(sunday)
        assertEquals(StartsAtRange(sunday.minusHours(LIVE_HOURS), LocalDateTime.of(2026, 10, 5, 0, 0)), range)
    }

    @Test
    fun `folding lowers and strips accents`() {
        assertEquals("sao joao", "  São JOÃO ".foldForSearch())
    }
}
