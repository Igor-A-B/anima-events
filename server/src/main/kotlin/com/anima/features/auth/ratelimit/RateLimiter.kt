package com.anima.features.auth.ratelimit

import java.util.concurrent.ConcurrentHashMap

// fixed-window counter per key, in memory (single instance only)
class RateLimiter(
    private val maxAttempts: Int,
    private val windowMillis: Long,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private class Window(val start: Long, var count: Int)

    private val windows = ConcurrentHashMap<String, Window>()

    // counts one attempt; false once the key went over the limit in the current window
    fun hit(key: String): Boolean {
        val now = clock()
        if (windows.size > MAX_KEYS) windows.values.removeIf { now - it.start >= windowMillis }
        var allowed = true
        windows.compute(key) { _, w ->
            val current = if (w == null || now - w.start >= windowMillis) Window(now, 0) else w
            current.count++
            allowed = current.count <= maxAttempts
            current
        }
        return allowed
    }

    fun isBlocked(key: String): Boolean {
        val w = windows[key] ?: return false
        return clock() - w.start < windowMillis && w.count >= maxAttempts
    }

    fun retryAfterSeconds(key: String): Long {
        val w = windows[key] ?: return 1
        return ((w.start + windowMillis - clock()) / 1000).coerceAtLeast(1)
    }

    fun reset(key: String) {
        windows.remove(key)
    }

    private companion object {
        const val MAX_KEYS = 10_000
    }
}
