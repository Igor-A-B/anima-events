package com.anima.features.event.services

import com.anima.features.event.entities.EventEntity
import com.anima.features.event.entities.EventImageEntity
import com.anima.features.event.exceptions.EventForbiddenException
import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.event.repositories.EventImageRepository
import com.anima.features.event.repositories.EventRepository
import com.anima.features.storage.services.ImageUpload
import com.anima.features.storage.services.StorageService
import com.anima.features.user.entities.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.mock.web.MockMultipartFile
import java.util.Optional
import java.util.UUID

private class FakeEvents(private val events: List<EventEntity>) : EventRepository {
    override fun findById(id: UUID): Optional<EventEntity> = Optional.ofNullable(events.find { it.id == id })
    override fun search(spec: Specification<EventEntity>, pageable: Pageable): Page<EventEntity> = throw NotImplementedError()
    override fun findAllByOrganizerId(organizerId: UUID) = events.filter { it.organizer?.id == organizerId }
    override fun save(event: EventEntity) = event
    override fun deleteById(id: UUID) = Unit
}

private class FakeImages(private val failing: Boolean = false) : EventImageRepository {
    val rows = mutableListOf<EventImageEntity>()
    override fun save(image: EventImageEntity): EventImageEntity {
        if (failing) throw IllegalStateException("db down")
        image.id = UUID.randomUUID()
        rows += image
        return image
    }
}

private class FakeStorage : StorageService {
    val files = mutableMapOf<String, String>()
    override fun upload(path: String, bytes: ByteArray, contentType: String) { files[path] = contentType }
    override fun delete(path: String) { files.remove(path) }
    override fun publicUrl(path: String) = "http://storage/$path"
}

class EventImageServiceTest {
    private val curator = UserEntity().apply { id = UUID.randomUUID() }
    private val event = EventEntity().apply {
        id = UUID.randomUUID()
        organizer = curator
    }
    private val storage = FakeStorage()
    private val images = FakeImages()
    private val service = EventImageService(FakeEvents(listOf(event)), images, storage)

    private fun image(contentType: String = "image/png", bytes: ByteArray = byteArrayOf(1, 2, 3)) =
        MockMultipartFile("file", "photo", contentType, bytes)

    @Test
    fun `curator uploads into the event folder`() {
        val result = service.add(curator.id!!, event.id!!, ImageUpload.from(image()))

        val path = storage.files.keys.single()
        assertTrue(path.startsWith("events/${event.id}/") && path.endsWith(".png"))
        assertEquals("http://storage/$path", result.url)
        assertEquals(path, images.rows.single().objectPath)
        assertEquals(images.rows.single().id.toString(), result.id)
    }

    @Test
    fun `only the curator can add images`() {
        assertThrows<EventForbiddenException> { service.add(UUID.randomUUID(), event.id!!, ImageUpload.from(image())) }
        assertTrue(storage.files.isEmpty())
    }

    @Test
    fun `unknown event is not found`() {
        assertThrows<EventNotFoundException> { service.add(curator.id!!, UUID.randomUUID(), ImageUpload.from(image())) }
    }

    @Test
    fun `file is removed when the row can't be saved`() {
        val failing = EventImageService(FakeEvents(listOf(event)), FakeImages(failing = true), storage)
        assertThrows<IllegalStateException> { failing.add(curator.id!!, event.id!!, ImageUpload.from(image())) }
        assertTrue(storage.files.isEmpty())
    }

    @Test
    fun `only non empty jpeg, png and webp are accepted`() {
        assertEquals("jpg", ImageUpload.from(image("image/jpeg")).extension)
        assertEquals("webp", ImageUpload.from(image("IMAGE/WEBP")).extension)
        assertThrows<IllegalArgumentException> { ImageUpload.from(image("text/plain")) }
        assertThrows<IllegalArgumentException> { ImageUpload.from(image(bytes = byteArrayOf())) }
    }
}
