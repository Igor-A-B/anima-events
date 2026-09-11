package com.example.anima.features.search.data

import com.example.anima.features.feed.domain.Event
import com.example.anima.features.search.domain.SearchFilters

// search data contract
// TODO: swap the implementation to go from mock to API
interface SearchRepository {
    suspend fun search(
        query: String = "",
        filters: SearchFilters = SearchFilters(),
    ): List<Event>
}
