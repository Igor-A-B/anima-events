package com.anima.features.event.repositories

import com.anima.features.event.entities.EventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class EventRepositoryImpl(private val jpa: EventJpaRepository) : EventRepository {
    override fun findById(id: UUID): Optional<EventEntity> = jpa.findById(id)
    override fun search(spec: Specification<EventEntity>, pageable: Pageable): Page<EventEntity> = jpa.findAll(spec, pageable)
    override fun save(event: EventEntity): EventEntity = jpa.save(event)
    override fun deleteById(id: UUID) = jpa.deleteById(id)
}

interface EventJpaRepository : JpaRepository<EventEntity, UUID>, JpaSpecificationExecutor<EventEntity>
