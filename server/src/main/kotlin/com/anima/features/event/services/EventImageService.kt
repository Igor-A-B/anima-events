package com.anima.features.event.services

import com.anima.features.event.entities.EventImageEntity
import com.anima.features.event.exceptions.EventForbiddenException
import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.event.models.EventImage
import com.anima.features.event.repositories.EventImageRepository
import com.anima.features.event.repositories.EventRepository
import com.anima.features.storage.services.ImageUpload
import com.anima.features.storage.services.StoragePaths
import com.anima.features.storage.services.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class EventImageService(
    private val events: EventRepository,
    private val images: EventImageRepository,
    private val storage: StorageService,
) {
    // only the curator (organizer) of the event can add images
    fun add(userId: UUID, eventId: UUID, image: ImageUpload): EventImage {
        val event = events.findById(eventId).orElseThrow { EventNotFoundException() }
        if (event.organizer?.id != userId) throw EventForbiddenException()

        val path = StoragePaths.eventImage(eventId, image.extension)
        storage.upload(path, image.bytes, image.contentType)
        val saved = try {
            images.save(EventImageEntity().apply {
                this.event = event
                objectPath = path
                contentType = image.contentType
            })
        } catch (e: RuntimeException) {
            // don't leave an unreferenced file behind
            storage.delete(path)
            throw e
        }
        return EventImage(saved.id.toString(), storage.publicUrl(path))
    }
}
