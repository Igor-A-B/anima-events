package com.example.anima.features.feed.data

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.FeedSectionType
import com.example.anima.features.feed.domain.FeedPage

// feed data contract, each section is searched on its own so rows load lazily
interface FeedRepository {
    // category == null means no filter, cursor == null means the first page
    suspend fun getSection(type: FeedSectionType, category: EventCategory? = null, cursor: String? = null): FeedPage

    suspend fun findById(id: String): Event?
}
