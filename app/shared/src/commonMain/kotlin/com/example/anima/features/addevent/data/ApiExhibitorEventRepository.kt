package com.example.anima.features.addevent.data

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.dtos.UpdateEventRequestDto
import com.anima.features.event.models.Event
import com.example.anima.core.log.AppLog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiExhibitorEventRepository(private val client: HttpClient) : ExhibitorEventRepository {

    private val _myEvents = MutableStateFlow<List<Event>>(emptyList())
    override val myEvents: StateFlow<List<Event>> = _myEvents.asStateFlow()

    override suspend fun refresh() {
        _myEvents.value = client.get("events/mine").body()
    }

    // the server orders by date and the date may have changed, so the list is fetched again
    override suspend fun create(request: CreateEventRequestDto): Event =
        client.post("events") { setBody(request) }.body<Event>().also { refreshQuietly() }

    override suspend fun getForm(id: String): UpdateEventRequestDto =
        client.get("events/$id/form").body()

    override suspend fun update(id: String, request: UpdateEventRequestDto): Event =
        client.put("events/$id") { setBody(request) }.body<Event>().also { refreshQuietly() }

    override suspend fun delete(id: String) {
        client.delete("events/$id")
        _myEvents.value = _myEvents.value.filter { it.id != id }
    }

    // the write already worked, a failed list refresh should not report it as failed
    private suspend fun refreshQuietly() {
        try {
            refresh()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLog.e("ExhibitorEvents", "refresh after a write failed", e)
        }
    }
}
