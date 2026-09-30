package com.example.anima.core.image

import kotlin.test.Test
import kotlin.test.assertEquals

class ImageUrlTest {
    private val url = "http://127.0.0.1:9199/v0/b/b/o/events%2Fa.jpg?alt=media"

    @Test
    fun loopback_follows_the_api_host() {
        assertEquals(
            "http://10.0.2.2:9199/v0/b/b/o/events%2Fa.jpg?alt=media",
            deviceImageUrl(url, "http://10.0.2.2:8080"),
        )
        assertEquals(
            "http://192.168.0.5:9199/v0/b/b/o/events%2Fa.jpg?alt=media",
            deviceImageUrl(url, "http://192.168.0.5:8080/"),
        )
    }

    @Test
    fun loopback_api_or_real_host_is_untouched() {
        assertEquals(url, deviceImageUrl(url, "http://localhost:8080"))
        val cdn = "https://firebasestorage.googleapis.com/v0/b/b/o/a?alt=media"
        assertEquals(cdn, deviceImageUrl(cdn, "http://10.0.2.2:8080"))
    }
}
