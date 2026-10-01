package com.anima.features.event.repositories

import com.anima.features.event.entities.EventImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class EventImageRepositoryImpl(private val jpa: EventImageJpaRepository) : EventImageRepository {
    override fun save(image: EventImageEntity): EventImageEntity = jpa.save(image)
}

interface EventImageJpaRepository : JpaRepository<EventImageEntity, UUID>
