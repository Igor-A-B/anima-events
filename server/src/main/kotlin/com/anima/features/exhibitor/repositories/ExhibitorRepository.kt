package com.anima.features.exhibitor.repositories
import com.anima.features.exhibitor.entities.ExhibitorEntity
import java.util.Optional
import java.util.UUID

interface ExhibitorRepository {
    fun findById(id: String): Optional<ExhibitorEntity>
    fun findByUserId(userId: UUID): Optional<ExhibitorEntity>
    fun existsByUserId(userId: UUID): Boolean
    fun save(exhibitor: ExhibitorEntity): ExhibitorEntity
    fun deleteById(id: String)
}
