package com.example.anima.features.eventdetail.presentation

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.EventStatus
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.user.models.AccountType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SubscriptionUiTest {

    private fun event(attendees: Int, capacity: Int?, status: EventStatus = EventStatus.UPCOMING) = Event(
        id = "1",
        title = "Jazz",
        category = EventCategory.MUSIC,
        venue = "v",
        city = "c",
        dateLabel = "d",
        timeLabel = "t",
        attendees = attendees,
        capacity = capacity,
        status = status,
    )

    private fun visitor(event: Event?, status: SubscriptionStatus? = null, busy: Boolean = false) =
        subscriptionUiFor(event, status, AccountType.VISITOR, busy)

    @Test
    fun attendees_equal_to_capacity_is_full() {
        assertEquals(SubscriptionUi.Full, visitor(event(attendees = 10, capacity = 10)))
    }

    @Test
    fun one_spot_left_can_still_subscribe() {
        assertEquals(SubscriptionUi.NotSubscribed, visitor(event(attendees = 9, capacity = 10)))
    }

    @Test
    fun over_capacity_is_full_too() {
        assertEquals(SubscriptionUi.Full, visitor(event(attendees = 11, capacity = 10)))
    }

    @Test
    fun confirmed_visitor_on_a_full_event_can_cancel() {
        assertEquals(SubscriptionUi.Confirmed, visitor(event(attendees = 10, capacity = 10), SubscriptionStatus.CONFIRMED))
    }

    @Test
    fun cancelled_visitor_on_a_full_event_sees_full() {
        assertEquals(SubscriptionUi.Full, visitor(event(attendees = 10, capacity = 10), SubscriptionStatus.CANCELLED))
    }

    @Test
    fun cancelled_visitor_with_room_can_subscribe_again() {
        assertEquals(SubscriptionUi.Cancelled, visitor(event(attendees = 3, capacity = 10), SubscriptionStatus.CANCELLED))
    }

    @Test
    fun no_capacity_is_never_full() {
        assertEquals(SubscriptionUi.NotSubscribed, visitor(event(attendees = 10_000, capacity = null)))
    }

    @Test
    fun finished_event_hides_the_button_before_full() {
        assertEquals(SubscriptionUi.Hidden, visitor(event(attendees = 10, capacity = 10, status = EventStatus.FINISHED)))
        assertEquals(
            SubscriptionUi.Attended,
            visitor(event(attendees = 10, capacity = 10, status = EventStatus.FINISHED), SubscriptionStatus.ATTENDED),
        )
    }

    @Test
    fun busy_and_non_visitors() {
        assertEquals(SubscriptionUi.Loading, visitor(event(attendees = 10, capacity = 10), busy = true))
        assertEquals(SubscriptionUi.Hidden, subscriptionUiFor(event(0, null), null, AccountType.EXHIBITOR, busy = false))
        assertEquals(SubscriptionUi.Hidden, subscriptionUiFor(event(0, null), null, null, busy = false))
        assertEquals(SubscriptionUi.Hidden, visitor(null))
    }

    @Test
    fun spots_left() {
        assertEquals(1, event(attendees = 9, capacity = 10).spotsLeft())
        assertEquals(0, event(attendees = 12, capacity = 10).spotsLeft())
        assertNull(event(attendees = 9, capacity = null).spotsLeft())
        assertTrue(event(attendees = 10, capacity = 10).isFull())
        assertFalse(event(attendees = 9, capacity = 10).isFull())
    }
}
