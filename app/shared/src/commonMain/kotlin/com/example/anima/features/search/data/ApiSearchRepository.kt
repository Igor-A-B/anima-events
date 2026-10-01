package com.example.anima.features.search.data

import com.anima.features.event.dtos.EventPageDto
import com.anima.features.event.models.DateFilter
import com.anima.features.event.models.PriceFilter
import com.example.anima.features.search.domain.SearchFilters
import com.example.anima.features.search.domain.SearchPage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

// same endpoint as the feed, without a section: all matching and paging happens on the server
class ApiSearchRepository(private val client: HttpClient) : SearchRepository {

    override suspend fun search(query: String, filters: SearchFilters, cursor: String?): SearchPage =
        client.get("events") {
            query.trim().takeIf { it.isNotEmpty() }?.let { parameter("q", it) }
            // repeated param, the server binds it to a list
            filters.categories.forEach { parameter("category", it.name) }
            if (filters.price != PriceFilter.ANY) parameter("price", filters.price.name)
            if (filters.date != DateFilter.ANY) parameter("date", filters.date.name)
            cursor?.let { parameter("cursor", it) }
            parameter("size", PAGE_SIZE)
        }.body<EventPageDto>().let { SearchPage(it.items, it.nextCursor) }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
