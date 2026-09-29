package com.anima.features.user.repositories
import com.anima.features.user.entities.UserEntity
import java.util.Optional
import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): Optional<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
    fun existsByEmail(email: String): Boolean
    fun save(user: UserEntity): UserEntity
    fun deleteById(id: UUID)
}