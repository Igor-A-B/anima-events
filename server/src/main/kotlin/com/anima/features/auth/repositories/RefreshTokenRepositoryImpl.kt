package com.anima.features.auth.repositories

import com.anima.features.auth.entities.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Repository
class RefreshTokenRepositoryImpl(private val jpa: RefreshTokenJpaRepository) : RefreshTokenRepository {
    override fun findByTokenHash(tokenHash: String): RefreshTokenEntity? = jpa.findByTokenHash(tokenHash)
    override fun findAllByUserId(userId: UUID): List<RefreshTokenEntity> = jpa.findAllByUserId(userId)
    override fun save(token: RefreshTokenEntity): RefreshTokenEntity = jpa.save(token)
    override fun deleteById(id: UUID) = jpa.deleteById(id)
    override fun deleteAllByUserId(userId: UUID) = jpa.deleteAllByUserId(userId)
    override fun revokeByTokenHash(tokenHash: String) = jpa.revokeByTokenHash(tokenHash)
    override fun revokeIfActive(tokenHash: String, now: Instant): Boolean = jpa.revokeIfActive(tokenHash, now) == 1
    override fun findActiveByUserId(userId: UUID, now: Instant): List<RefreshTokenEntity> =
        jpa.findAllByUserIdAndRevokedFalseAndExpiresAtAfterOrderByCreatedAtAsc(userId, now)
    override fun deleteExpired(now: Instant): Int = jpa.deleteExpired(now)
}

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, UUID> {
    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?
    fun findAllByUserId(userId: UUID): List<RefreshTokenEntity>
    fun findAllByUserIdAndRevokedFalseAndExpiresAtAfterOrderByCreatedAtAsc(userId: UUID, now: Instant): List<RefreshTokenEntity>

    @Transactional
    fun deleteAllByUserId(userId: UUID)

    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity t SET t.revoked = true WHERE t.tokenHash = :tokenHash")
    fun revokeByTokenHash(@Param("tokenHash") tokenHash: String)

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE RefreshTokenEntity t SET t.revoked = true WHERE t.tokenHash = :tokenHash AND t.revoked = false AND t.expiresAt > :now")
    fun revokeIfActive(@Param("tokenHash") tokenHash: String, @Param("now") now: Instant): Int

    // revoked rows are kept until they expire so a replayed token is still recognised as reuse
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenEntity t WHERE t.expiresAt < :now")
    fun deleteExpired(@Param("now") now: Instant): Int
}
