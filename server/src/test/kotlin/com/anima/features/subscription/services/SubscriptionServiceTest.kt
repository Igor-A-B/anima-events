package com.anima.features.subscription.services

import com.anima.features.event.entities.EventEntity
import com.anima.features.event.entities.EventImageEntity
import com.anima.features.subscription.exceptions.EventAlreadyFinishedException
import com.anima.features.subscription.exceptions.EventFullException
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.subscription.routes.SubscriptionRoute
import com.anima.features.user.entities.UserEntity
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.testing.FakeEventRepository
import com.anima.testing.FakeStorageService
import com.anima.testing.FakeSubscriptionRepository
import com.anima.testing.FakeVisitorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.anima.features.subscription.entities.SubscriptionEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.UUID

class SubscriptionServiceTest {
    private fun visitor(name: String) = VisitorEntity().apply {
        id = "visitor-$name"
        user = UserEntity().apply { id = UUID.randomUUID() }
    }

    private val me = visitor("me")
    private val other1 = visitor("1")
    private val other2 = visitor("2")

    private fun event(capacity: Int?, startsAt: LocalDateTime = LocalDateTime.now().plusDays(3)) = EventEntity().apply {
        id = UUID.randomUUID()
        title = "Jazz"
        this.capacity = capacity
        this.startsAt = startsAt
        organizer = UserEntity().apply { id = UUID.randomUUID(); name = "Org" }
    }

    private val subs = FakeSubscriptionRepository()

    private fun service(vararg events: EventEntity) =
        SubscriptionService(subs, FakeVisitorRepository(me, other1, other2), FakeEventRepository(*events), FakeStorageService())

    private fun activeCount(event: EventEntity) =
        subs.countByEventIdAndStatusIn(event.id!!, listOf(SubscriptionStatus.CONFIRMED, SubscriptionStatus.ATTENDED))

    @Test
    fun `last spot can be taken, the event becomes exactly full`() {
        val event = event(capacity = 2)
        subs.add(other1, event, SubscriptionStatus.CONFIRMED)

        val result = service(event).subscribe(me.user!!.id!!, event.id!!)

        assertEquals(SubscriptionStatus.CONFIRMED, result.status)
        assertEquals(2L, activeCount(event))
        assertEquals(2, result.event.attendees)
        assertEquals(2, result.event.capacity)
    }

    @Test
    fun `full event rejects a new visitor and saves nothing`() {
        val event = event(capacity = 2)
        subs.add(other1, event, SubscriptionStatus.CONFIRMED)
        subs.add(other2, event, SubscriptionStatus.ATTENDED)

        assertThrows<EventFullException> { service(event).subscribe(me.user!!.id!!, event.id!!) }

        assertEquals(0, subs.saves)
        assertEquals(2, subs.rows.size)
    }

    @Test
    fun `cancelled subscriptions do not take a spot`() {
        val event = event(capacity = 1)
        subs.add(other1, event, SubscriptionStatus.CANCELLED)

        val result = service(event).subscribe(me.user!!.id!!, event.id!!)

        assertEquals(SubscriptionStatus.CONFIRMED, result.status)
    }

    @Test
    fun `no capacity means unlimited`() {
        val event = event(capacity = null)
        subs.add(other1, event, SubscriptionStatus.CONFIRMED)
        subs.add(other2, event, SubscriptionStatus.CONFIRMED)

        service(event).subscribe(me.user!!.id!!, event.id!!)

        assertEquals(3L, activeCount(event))
    }

    @Test
    fun `already subscribed visitor on a full event gets the existing subscription`() {
        val event = event(capacity = 1)
        val existing = subs.add(me, event, SubscriptionStatus.CONFIRMED)

        val result = service(event).subscribe(me.user!!.id!!, event.id!!)

        assertEquals(existing.id.toString(), result.id)
        assertEquals(0, subs.saves)
        assertEquals(1, subs.rows.size)
    }

    @Test
    fun `cancelled visitor coming back to a full event is rejected`() {
        val event = event(capacity = 1)
        val mine = subs.add(me, event, SubscriptionStatus.CANCELLED)
        subs.add(other1, event, SubscriptionStatus.CONFIRMED)

        assertThrows<EventFullException> { service(event).subscribe(me.user!!.id!!, event.id!!) }

        assertEquals(SubscriptionStatus.CANCELLED, mine.status)
        assertEquals(0, subs.saves)
    }

    @Test
    fun `cancelled visitor reuses the row when there is room`() {
        val event = event(capacity = 2)
        val mine = subs.add(me, event, SubscriptionStatus.CANCELLED)
        subs.add(other1, event, SubscriptionStatus.CONFIRMED)

        val result = service(event).subscribe(me.user!!.id!!, event.id!!)

        assertEquals(mine.id.toString(), result.id)
        assertEquals(2, subs.rows.size)
    }

    @Test
    fun `finished event is still rejected`() {
        val event = event(capacity = null, startsAt = LocalDateTime.now().minusDays(2))
        assertThrows<EventAlreadyFinishedException> { service(event).subscribe(me.user!!.id!!, event.id!!) }
    }

    @Test
    fun `subscribe locks the event row`() {
        val event = event(capacity = 5)
        val events = FakeEventRepository(event)
        SubscriptionService(subs, FakeVisitorRepository(me), events, FakeStorageService()).subscribe(me.user!!.id!!, event.id!!)

        assertEquals(listOf(event.id), events.lockedIds)
    }

    @Test
    fun `subscription list carries the event images and capacity`() {
        val event = event(capacity = 10)
        event.images += EventImageEntity().apply { objectPath = "events/a.jpg"; this.event = event }
        event.images += EventImageEntity().apply { objectPath = "events/b.jpg"; this.event = event }
        subs.add(me, event, SubscriptionStatus.CONFIRMED)

        val list = service(event).listMine(me.user!!.id!!)

        assertEquals(listOf("http://x/events/a.jpg", "http://x/events/b.jpg"), list.single().event.imageUrls)
        assertEquals(10, list.single().event.capacity)
        assertEquals(1, list.single().event.attendees)
    }

    @Test
    fun `route answers a full event with 409`() {
        val event = event(capacity = 1)
        subs.add(other1, event, SubscriptionStatus.CONFIRMED)
        val mvc = MockMvcBuilders.standaloneSetup(SubscriptionRoute(service(event))).build()

        mvc.post("/events/${event.id}/subscription") {
            principal = UsernamePasswordAuthenticationToken(me.user!!.id.toString(), null, emptyList())
        }.andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("This event is full") }
        }
    }

    @Test
    fun `route retries a duplicate insert and answers the existing subscription`() {
        val event = event(capacity = null)
        // the first save loses the race: another request of the same visitor committed the row first
        val racing = object : FakeSubscriptionRepository() {
            var collided = false
            override fun onSave(subscription: SubscriptionEntity) {
                if (collided) return
                collided = true
                add(me, event, SubscriptionStatus.CONFIRMED)
                throw DataIntegrityViolationException("duplicate visitor_id, event_id")
            }
        }
        val service = SubscriptionService(racing, FakeVisitorRepository(me), FakeEventRepository(event), FakeStorageService())
        val mvc = MockMvcBuilders.standaloneSetup(SubscriptionRoute(service)).build()

        mvc.post("/events/${event.id}/subscription") {
            principal = UsernamePasswordAuthenticationToken(me.user!!.id.toString(), null, emptyList())
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(racing.rows.single().id.toString()) }
            jsonPath("$.status") { value("CONFIRMED") }
        }
    }

    @Test
    fun `route answers a repeated collision with a neutral 500, not 409`() {
        val event = event(capacity = null)
        val alwaysColliding = object : FakeSubscriptionRepository() {
            override fun onSave(subscription: SubscriptionEntity) {
                throw DataIntegrityViolationException("duplicate")
            }
        }
        val service = SubscriptionService(alwaysColliding, FakeVisitorRepository(me), FakeEventRepository(event), FakeStorageService())
        val mvc = MockMvcBuilders.standaloneSetup(SubscriptionRoute(service)).build()

        mvc.post("/events/${event.id}/subscription") {
            principal = UsernamePasswordAuthenticationToken(me.user!!.id.toString(), null, emptyList())
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("$.error") { exists() }
        }
    }
}
