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
    fun legacy_image_url_is_the_fallback_cover() {
        val url = "https://cdn.example.com/legacy.jpg"
        assertEquals(url, event.copy(imageUrl = url).coverImageUrl("http://10.0.2.2:8080"))
    }

    @Test
    fun uploaded_image_wins_over_the_legacy_url() {
        val uploaded = "https://firebasestorage.googleapis.com/v0/b/b/o/a.jpg?alt=media"
        assertEquals(
            uploaded,
            event.copy(imageUrl = "https://cdn.example.com/legacy.jpg", imageUrls = listOf(uploaded)).coverImageUrl("http://10.0.2.2:8080"),
        )
    }

    @Test
    fun loopback_cover_follows_the_api_host() {
        assertEquals(
            "http://10.0.2.2:9199/b/a.jpg",
            event.copy(imageUrl = "http://127.0.0.1:9199/b/a.jpg").coverImageUrl("http://10.0.2.2:8080"),
        )
    }

    @Test
    fun blank_legacy_url_keeps_the_gradient() {
        assertNull(event.copy(imageUrl = " ").coverImageUrl("http://10.0.2.2:8080"))
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
