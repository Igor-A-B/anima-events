package com.anima.features.auth.services

import com.anima.features.auth.repositories.RefreshTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class RefreshTokenCleanup(private val refreshTokens: RefreshTokenRepository) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 30 3 * * *")
    fun purgeExpired() {
        val removed = refreshTokens.deleteExpired(Instant.now())
        log.info("Purged {} expired refresh tokens", removed)
    }
}
