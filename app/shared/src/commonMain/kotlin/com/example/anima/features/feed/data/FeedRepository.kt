package com.example.anima.features.feed.data

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.example.anima.features.feed.domain.FeedSectionType

// feed data contract, each section is searched on its own so rows load lazily
interface FeedRepository {
    // category == null means no filter
    suspend fun getSection(type: FeedSectionType, category: EventCategory? = null, page: Int = 0): List<Event>

    suspend fun findById(id: String): Event?
}
