package com.anima.features.event.repositories

import com.anima.features.event.entities.EventEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.util.Optional
import java.util.UUID

interface EventRepository {
    fun findById(id: UUID): Optional<EventEntity>
    fun search(spec: Specification<EventEntity>, pageable: Pageable): Page<EventEntity>
    fun save(event: EventEntity): EventEntity
    fun findAllByOrganizerId(organizerId: UUID): List<EventEntity>
    fun deleteById(id: UUID)
}
