package com.anima.features.visitor.repositories
import com.anima.features.visitor.entities.VisitorEntity
import java.util.Optional
import java.util.UUID

interface VisitorRepository {
    fun findById(id: String): Optional<VisitorEntity>
    fun findByUserId(userId: UUID): Optional<VisitorEntity>
    fun existsByUserId(userId: UUID): Boolean
    fun save(visitor: VisitorEntity): VisitorEntity
    fun deleteById(id: String)
}
