package com.anima.features.auth.repositories
import com.anima.features.auth.entities.RefreshTokenEntity
import java.util.UUID

interface RefreshTokenRepository {
    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?
    fun findAllByUserId(userId: UUID): List<RefreshTokenEntity>
    fun save(token: RefreshTokenEntity): RefreshTokenEntity
    fun deleteById(id: UUID)
    fun deleteAllByUserId(userId: UUID)
    fun revokeByTokenHash(tokenHash: String)
}