package com.example.anima.features.eventdetail.data

import com.anima.features.event.models.EventImage
import com.example.anima.core.events.EventChanges
import com.example.anima.core.image.ImageUploader
import com.example.anima.core.image.PickedImage

class ApiEventImageRepository(
    private val uploader: ImageUploader,
    private val eventChanges: EventChanges,
) : EventImageRepository {

    override suspend fun upload(eventId: String, image: PickedImage): EventImage =
        uploader.upload<EventImage>("events/$eventId/images", image).also { eventChanges.notifyChanged() }
}
