package com.example.anima.features.feed.data

import com.anima.features.event.dtos.EventPageDto
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.example.anima.core.network.ApiException
import com.example.anima.features.feed.domain.FeedSectionType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiFeedRepository(private val client: HttpClient) : FeedRepository {

    override suspend fun getSection(type: FeedSectionType, category: EventCategory?, page: Int): List<Event> =
        client.get("events") {
            parameter("section", type.name)
            category?.let { parameter("category", it.name) }
            parameter("page", page)
            // TODO: use the device location, this is the center of Sao Paulo
            if (type == FeedSectionType.NEARBY) {
                parameter("lat", DEFAULT_LAT)
                parameter("lng", DEFAULT_LNG)
            }
        }.body<EventPageDto>().items

    override suspend fun findById(id: String): Event? =
        try {
            client.get("events/$id").body<Event>()
        } catch (e: ApiException) {
            if (e.status == 404) null else throw e
        }

    private companion object {
        const val DEFAULT_LAT = -23.55
        const val DEFAULT_LNG = -46.63
    }
}
