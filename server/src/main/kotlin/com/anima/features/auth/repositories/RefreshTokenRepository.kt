package com.anima.features.auth.repositories
import com.anima.features.auth.entities.RefreshTokenEntity
import java.time.Instant
import java.util.UUID

interface RefreshTokenRepository {
    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?
    fun findAllByUserId(userId: UUID): List<RefreshTokenEntity>
    fun save(token: RefreshTokenEntity): RefreshTokenEntity
    fun deleteById(id: UUID)
    fun deleteAllByUserId(userId: UUID)
    fun revokeByTokenHash(tokenHash: String)

    // atomic: true only for the single caller that flipped an active token to revoked
    fun revokeIfActive(tokenHash: String, now: Instant): Boolean
    fun findActiveByUserId(userId: UUID, now: Instant): List<RefreshTokenEntity>
    fun deleteExpired(now: Instant): Int
}
