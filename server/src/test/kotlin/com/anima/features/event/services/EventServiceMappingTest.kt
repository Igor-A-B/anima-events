package com.anima.features.event.services

import com.anima.features.event.entities.EventEntity
import com.anima.features.event.entities.EventImageEntity
import com.anima.features.exhibitor.entities.ExhibitorEntity
import com.anima.features.exhibitor.repositories.ExhibitorRepository
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.user.entities.UserEntity
import com.anima.features.user.repositories.UserRepository
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.testing.FakeEventRepository
import com.anima.testing.FakeStorageService
import com.anima.testing.FakeSubscriptionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

private class NoUsers : UserRepository {
    override fun findById(id: UUID): Optional<UserEntity> = Optional.empty()
    override fun findByEmail(email: String): Optional<UserEntity> = Optional.empty()
    override fun existsByEmail(email: String) = false
    override fun save(user: UserEntity) = user
    override fun deleteById(id: UUID) = Unit
}

private class NoExhibitors : ExhibitorRepository {
    override fun findById(id: String): Optional<ExhibitorEntity> = Optional.empty()
    override fun findByUserId(userId: UUID): Optional<ExhibitorEntity> = Optional.empty()
    override fun existsByUserId(userId: UUID) = false
    override fun save(exhibitor: ExhibitorEntity) = exhibitor
    override fun deleteById(id: String) = Unit
}

class EventServiceMappingTest {
    private val organizer = UserEntity().apply { id = UUID.randomUUID(); name = "Org" }
    private val event = EventEntity().apply {
        id = UUID.randomUUID()
        title = "Jazz"
        startsAt = LocalDateTime.now().plusDays(3)
        capacity = 30
        price = 10.0
        organizer = this@EventServiceMappingTest.organizer
    }
    private val subs = FakeSubscriptionRepository()
    private val service = EventService(FakeEventRepository(event), NoUsers(), subs, NoExhibitors(), FakeStorageService())

    private fun addImage(path: String) {
        event.images += EventImageEntity().apply { objectPath = path; this.event = this@EventServiceMappingTest.event }
    }

    @Test
    fun `detail carries the uploaded images, capacity and price`() {
        addImage("events/a.jpg")
        subs.add(VisitorEntity().apply { id = "v1" }, event, SubscriptionStatus.CONFIRMED)

        val result = service.get(event.id!!)

        assertEquals(listOf("http://x/events/a.jpg"), result.imageUrls)
        assertEquals(30, result.capacity)
        assertEquals(1, result.attendees)
        assertEquals("R$ 10.00", result.price)
    }

    @Test
    fun `edit form exposes the uploaded cover without touching imageUrl`() {
        addImage("events/a.jpg")

        val form = service.form(organizer.id!!, event.id!!)

        assertEquals("http://x/events/a.jpg", form.coverUrl)
        assertNull(form.imageUrl)
    }

    @Test
    fun `edit form falls back to the legacy imageUrl`() {
        event.imageUrl = "https://cdn/legacy.jpg"

        val form = service.form(organizer.id!!, event.id!!)

        assertEquals("https://cdn/legacy.jpg", form.coverUrl)
        assertEquals("https://cdn/legacy.jpg", form.imageUrl)
    }
}
