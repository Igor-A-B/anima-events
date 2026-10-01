package com.example.anima.features.search.data

import com.example.anima.features.search.domain.SearchFilters
import com.example.anima.features.search.domain.SearchPage

// search data contract, cursor == null means the first page
interface SearchRepository {
    suspend fun search(
        query: String = "",
        filters: SearchFilters = SearchFilters(),
        cursor: String? = null,
    ): SearchPage
}
