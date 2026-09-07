package com.example.anima.features.search.data

import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventStatus
import com.example.anima.features.search.domain.DateFilter
import com.example.anima.features.search.domain.PriceFilter

/*
 * TEMPORARY MATCHING LAYER
 * When the backend lands, Event gains numeric/typed fields (priceCents,
 * startsAt) and this whole file goes away: the matching moves to the query.
 * Everything fragile is deliberately kept here and nowhere else.
 */

// accent folding
private const val ACCENTED = "áàâãäéèêëíìîïóòôõöúùûüçñ"
private const val PLAIN = "aaaaaeeeeiiiiooooouuuucn"

internal fun String.foldForSearch(): String = lowercase()
    .map { char ->
        val index = ACCENTED.indexOf(char)
        if (index >= 0) PLAIN[index] else char
    }
    .joinToString(separator = "")
    .trim()

internal fun Event.matchesQuery(query: String): Boolean {
    val needle = query.foldForSearch()
    if (needle.isEmpty()) return true

    return listOf(title, venue, city, organizerName)
        .any { field -> field.foldForSearch().contains(needle) }
}

internal fun Event.matchesPrice(filter: PriceFilter): Boolean = when (filter) {
    PriceFilter.ANY -> true
    PriceFilter.FREE -> price == null
    PriceFilter.PAID -> price != null
}

internal fun Event.matchesDate(filter: DateFilter): Boolean {
    val label = dateLabel.foldForSearch()

    return when (filter) {
        DateFilter.ANY -> true
        DateFilter.NOW -> status == EventStatus.OCCURRING
        DateFilter.TODAY -> status == EventStatus.OCCURRING || label in TODAY_LABELS
        DateFilter.TOMORROW -> label in TOMORROW_LABELS
        DateFilter.WEEKEND -> WEEKEND_PREFIXES.any { prefix -> label.startsWith(prefix) }
    }
}

private val TODAY_LABELS = setOf("agora", "hoje", "now", "today", "ahora", "hoy")
private val TOMORROW_LABELS = setOf("amanha", "tomorrow", "manana")
private val WEEKEND_PREFIXES = listOf("sab", "dom", "sat", "sun")
