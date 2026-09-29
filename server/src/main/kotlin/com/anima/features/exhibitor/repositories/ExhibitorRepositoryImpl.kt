package com.anima.features.exhibitor.repositories

import com.anima.features.exhibitor.entities.ExhibitorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class ExhibitorRepositoryImpl(private val jpa: ExhibitorJpaRepository) : ExhibitorRepository {
    override fun findById(id: String): Optional<ExhibitorEntity> = jpa.findById(id)
    override fun findByUserId(userId: UUID): Optional<ExhibitorEntity> = jpa.findByUserId(userId)
    override fun existsByUserId(userId: UUID): Boolean = jpa.existsByUserId(userId)
    override fun save(exhibitor: ExhibitorEntity): ExhibitorEntity = jpa.save(exhibitor)
    override fun deleteById(id: String) = jpa.deleteById(id)
}

interface ExhibitorJpaRepository : JpaRepository<ExhibitorEntity, String> {
    fun findByUserId(userId: UUID): Optional<ExhibitorEntity>
    fun existsByUserId(userId: UUID): Boolean
}
