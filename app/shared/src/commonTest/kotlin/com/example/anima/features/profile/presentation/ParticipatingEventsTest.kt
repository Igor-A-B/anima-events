package com.example.anima.features.profile.presentation

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.EventStatus
import com.anima.features.subscription.models.Subscription
import com.anima.features.subscription.models.SubscriptionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParticipatingEventsTest {

    private fun sub(
        id: String,
        status: EventStatus,
        at: Long,
        subscription: SubscriptionStatus = SubscriptionStatus.CONFIRMED,
    ) = Subscription(
        id = "s-$id",
        visitorId = "v1",
        event = Event(
            id = id,
            title = id,
            category = EventCategory.MUSIC,
            venue = "v",
            city = "c",
            dateLabel = "d",
            timeLabel = "t",
            status = status,
        ),
        createdTimestamp = at,
        status = subscription,
    )

    @Test
    fun emptyInputGivesEmptyList() {
        assertTrue(participatingEvents(emptyList()).isEmpty())
    }

    @Test
    fun cancelledSubscriptionsAreDropped() {
        val result = participatingEvents(
            listOf(
                sub("a", EventStatus.UPCOMING, 1, SubscriptionStatus.CANCELLED),
                sub("b", EventStatus.UPCOMING, 2),
                sub("c", EventStatus.UPCOMING, 3, SubscriptionStatus.ATTENDED),
            )
        )
        assertEquals(listOf("c", "b"), result.map { it.id })
    }

    @Test
    fun liveThenUpcomingThenEnded() {
        val result = participatingEvents(
            listOf(
                sub("ended", EventStatus.FINISHED, 30),
                sub("upcoming", EventStatus.UPCOMING, 20),
                sub("live", EventStatus.OCCURRING, 10),
            )
        )
        assertEquals(listOf("live", "upcoming", "ended"), result.map { it.id })
    }

    @Test
    fun newestSubscriptionFirstInsideAGroup() {
        val result = participatingEvents(
            listOf(
                sub("old", EventStatus.UPCOMING, 1),
                sub("new", EventStatus.UPCOMING, 5),
                sub("mid", EventStatus.UPCOMING, 3),
                sub("ended", EventStatus.FINISHED, 9),
            )
        )
        assertEquals(listOf("new", "mid", "old", "ended"), result.map { it.id })
    }
}
