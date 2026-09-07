package com.example.anima.features.search.data

import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.data.MockFeedRepository
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.search.domain.SearchFilters
import kotlinx.coroutines.delay

/**
 * Search over the same mock catalogue the feed uses.
 * It reads through FeedRepository. The sections are flattened and
 * de-duplicated: an event can sit in more than one feed row, but it must appear
 * only once in a result grid.
 */
class MockSearchRepository(
    private val feedRepository: FeedRepository = MockFeedRepository(),
) : SearchRepository {

    override suspend fun search(query: String, filters: SearchFilters): List<Event> {
        delay(300)

        return feedRepository.getSections()
            .flatMap { section -> section.events }
            .distinctBy { event -> event.id }
            .filter { event -> event.matches(query, filters) }
    }

    private fun Event.matches(query: String, filters: SearchFilters): Boolean {
        val categoryMatches = filters.categories.isEmpty() || category in filters.categories

        return matchesQuery(query) &&
            categoryMatches &&
            matchesPrice(filters.price) &&
            matchesDate(filters.date)
    }
}
