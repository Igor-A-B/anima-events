package com.example.anima.features.feed.presentation.components

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EventCoverTest {

    private val event = Event(
        id = "1",
        title = "Jazz",
        category = EventCategory.MUSIC,
        venue = "Galpao 9",
        city = "Sao Paulo",
        dateLabel = "05 OUT",
        timeLabel = "20:30",
    )

    @Test
    fun cover_is_the_first_image() {
        val url = "https://firebasestorage.googleapis.com/v0/b/b/o/a.jpg?alt=media"
        assertEquals(url, event.copy(imageUrls = listOf(url, "http://x/b.jpg")).coverImageUrl("http://10.0.2.2:8080"))
    }

    @Test
    fun no_images_keeps_the_gradient_only() {
        assertNull(event.coverImageUrl("http://10.0.2.2:8080"))
    }

    @Test
    fun gradient_accepts_any_seed() {
        eventCoverBrush(-1)
        eventCoverBrush(6)
    }
}
