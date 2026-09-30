package com.anima.features.auth.ratelimit

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RateLimiterTest {
    private var now = 0L
    private val limiter = RateLimiter(maxAttempts = 3, windowMillis = 1_000, clock = { now })

    @Test
    fun `allows up to the limit then rejects`() {
        repeat(3) { assertTrue(limiter.hit("k")) }
        assertFalse(limiter.hit("k"))
    }

    @Test
    fun `window expiry starts a fresh count`() {
        repeat(4) { limiter.hit("k") }
        now = 1_000
        assertTrue(limiter.hit("k"))
    }

    @Test
    fun `keys are independent and reset clears a key`() {
        repeat(3) { limiter.hit("a") }
        assertTrue(limiter.isBlocked("a"))
        assertFalse(limiter.isBlocked("b"))
        limiter.reset("a")
        assertFalse(limiter.isBlocked("a"))
    }
}
