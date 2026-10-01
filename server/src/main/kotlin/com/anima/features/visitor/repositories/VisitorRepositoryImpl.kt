package com.anima.features.visitor.repositories

import com.anima.features.visitor.entities.VisitorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class VisitorRepositoryImpl(private val jpa: VisitorJpaRepository) : VisitorRepository {
    override fun findById(id: String): Optional<VisitorEntity> = jpa.findById(id)
    override fun findByUserId(userId: UUID): Optional<VisitorEntity> = jpa.findByUserId(userId)
    override fun existsByUserId(userId: UUID): Boolean = jpa.existsByUserId(userId)
    override fun save(visitor: VisitorEntity): VisitorEntity = jpa.save(visitor)
    override fun deleteById(id: String) = jpa.deleteById(id)
}

interface VisitorJpaRepository : JpaRepository<VisitorEntity, String> {
    fun findByUserId(userId: UUID): Optional<VisitorEntity>
    fun existsByUserId(userId: UUID): Boolean
}
