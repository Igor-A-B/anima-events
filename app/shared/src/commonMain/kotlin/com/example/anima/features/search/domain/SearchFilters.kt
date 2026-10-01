package com.example.anima.features.search.domain

import com.anima.features.event.models.DateFilter
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.PriceFilter

/** Everything the search narrows by, apart from the free text query. */
data class SearchFilters(
    val categories: Set<EventCategory> = emptySet(),
    val price: PriceFilter = PriceFilter.ANY,
    val date: DateFilter = DateFilter.ANY,
    val radiusKm: Int? = null,
) {
    val activeCount: Int =
        categories.size +
            (if (price != PriceFilter.ANY) 1 else 0) +
            (if (date != DateFilter.ANY) 1 else 0) +
            (if (radiusKm != null) 1 else 0)

    val isDefault: Boolean = activeCount == 0

    fun toggleCategory(category: EventCategory): SearchFilters = copy(
        categories = if (category in categories) categories - category else categories + category,
    )
}
