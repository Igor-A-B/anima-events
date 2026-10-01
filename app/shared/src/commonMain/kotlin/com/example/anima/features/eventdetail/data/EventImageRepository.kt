package com.example.anima.features.eventdetail.data

import com.anima.features.event.models.EventImage
import com.example.anima.core.image.PickedImage

// images of an event, only its curator can add them
interface EventImageRepository {
    // throws ApiException: 400 bad type, 403 not the curator, 404 no event, 413 too large
    suspend fun upload(eventId: String, image: PickedImage): EventImage
}
