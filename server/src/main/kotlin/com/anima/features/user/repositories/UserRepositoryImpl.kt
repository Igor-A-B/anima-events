package com.anima.features.user.repositories

import com.anima.features.user.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class UserRepositoryImpl(private val jpa: UserJpaRepository) : UserRepository {
    override fun findById(id: UUID): Optional<UserEntity> = jpa.findById(id)
    override fun findByEmail(email: String): Optional<UserEntity> = jpa.findByEmail(email)
    override fun existsByEmail(email: String): Boolean = jpa.existsByEmail(email)
    override fun save(user: UserEntity): UserEntity = jpa.save(user)
    override fun deleteById(id: UUID) = jpa.deleteById(id)
}

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    override fun findById(id: UUID): Optional<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
    fun existsByEmail(email: String): Boolean
}