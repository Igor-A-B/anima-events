package com.anima.features.event.repositories

import com.anima.features.event.entities.EventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class EventRepositoryImpl(private val jpa: EventJpaRepository) : EventRepository {
    override fun findById(id: UUID): Optional<EventEntity> = jpa.findById(id)
    override fun findByIdForUpdate(id: UUID): Optional<EventEntity> = jpa.findByIdForUpdate(id)
    override fun search(spec: Specification<EventEntity>, pageable: Pageable): Page<EventEntity> = jpa.findAll(spec, pageable)
    override fun save(event: EventEntity): EventEntity = jpa.save(event)
    override fun findAllByOrganizerId(organizerId: UUID): List<EventEntity> =
        jpa.findAllByOrganizerIdOrderByStartsAtDesc(organizerId)
    override fun deleteById(id: UUID) = jpa.deleteById(id)
}

interface EventJpaRepository : JpaRepository<EventEntity, UUID>, JpaSpecificationExecutor<EventEntity> {
    fun findAllByOrganizerIdOrderByStartsAtDesc(organizerId: UUID): List<EventEntity>

    // SELECT ... FOR UPDATE, must run inside a transaction
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventEntity e where e.id = :id")
    fun findByIdForUpdate(@Param("id") id: UUID): Optional<EventEntity>
}
