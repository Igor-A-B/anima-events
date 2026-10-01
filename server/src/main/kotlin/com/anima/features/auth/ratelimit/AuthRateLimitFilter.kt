package com.anima.features.auth.ratelimit

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

// throttles the unauthenticated token endpoints per client address
class AuthRateLimitFilter(
    private val limiter: RateLimiter = RateLimiter(maxAttempts = 20, windowMillis = 60_000),
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.method != "POST" || request.servletPath !in LIMITED_PATHS

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        // remoteAddr is the real client only when the app is not behind a proxy
        val key = "${request.remoteAddr}|${request.servletPath}"
        if (!limiter.hit(key)) {
            response.status = 429
            response.setHeader("Retry-After", limiter.retryAfterSeconds(key).toString())
            response.contentType = "application/json"
            response.writer.write("""{"error":"Too many requests, try again later"}""")
            return
        }
        chain.doFilter(request, response)
    }

    private companion object {
        val LIMITED_PATHS = setOf("/auth/login", "/auth/refresh")
    }
}
