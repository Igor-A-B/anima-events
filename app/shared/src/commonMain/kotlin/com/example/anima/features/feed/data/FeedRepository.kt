package com.example.anima.features.feed.data

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.example.anima.features.feed.domain.FeedSection

// feed data contract
// TODO: swap the implementation to go from mock to API
interface FeedRepository {
    // category == null means no filter
    suspend fun getSections(category: EventCategory? = null): List<FeedSection>

    suspend fun findById(id: String): Event?

}
