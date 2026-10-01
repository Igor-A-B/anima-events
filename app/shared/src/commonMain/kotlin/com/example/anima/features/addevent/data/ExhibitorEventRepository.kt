package com.example.anima.features.addevent.data

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.dtos.UpdateEventRequestDto
import com.anima.features.event.models.Event
import kotlinx.coroutines.flow.StateFlow

// the exhibitor's side of events: the ones they organize, and changing them
// observable because the profile lists them while the form changes them
// every call throws ApiException, 403 means the caller is not an exhibitor or not the organizer
interface ExhibitorEventRepository {
    // newest first, past ones included
    val myEvents: StateFlow<List<Event>>

    suspend fun refresh()

    suspend fun create(request: CreateEventRequestDto): Event

    // stored values, to start the edit form from
    suspend fun getForm(id: String): UpdateEventRequestDto

    suspend fun update(id: String, request: UpdateEventRequestDto): Event

    suspend fun delete(id: String)
}
