package com.anima.features.event.services

import com.anima.features.event.models.DateFilter
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

// startsAt window for a date filter, from inclusive, until exclusive, null means no bound
internal data class StartsAtRange(val from: LocalDateTime?, val until: LocalDateTime?)

// live events started up to LIVE_HOURS ago, so every window but ANY opens at now - LIVE_HOURS
internal fun DateFilter.startsAtRange(now: LocalDateTime): StartsAtRange {
    val liveStart = now.minusHours(LIVE_HOURS)
    val today = now.toLocalDate().atStartOfDay()
    val tomorrow = today.plusDays(1)
    return when (this) {
        DateFilter.ANY -> StartsAtRange(null, null)
        DateFilter.NOW -> StartsAtRange(liveStart, now.plusNanos(1))
        DateFilter.TODAY -> StartsAtRange(liveStart, tomorrow)
        DateFilter.TOMORROW -> StartsAtRange(tomorrow, tomorrow.plusDays(1))
        DateFilter.WEEKEND -> {
            val monday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            val saturday = monday.minusDays(2)
            StartsAtRange(maxOf(liveStart, saturday), monday)
        }
    }
}

// accent folding, the same table is applied to the column with the sql translate() function
internal const val ACCENTED = "áàâãäéèêëíìîïóòôõöúùûüçñ"
internal const val PLAIN = "aaaaaeeeeiiiiooooouuuucn"

internal fun String.foldForSearch(): String =
    lowercase().map { c -> ACCENTED.indexOf(c).let { if (it >= 0) PLAIN[it] else c } }.joinToString("").trim()
