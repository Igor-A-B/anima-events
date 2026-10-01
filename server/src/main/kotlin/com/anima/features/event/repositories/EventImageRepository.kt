package com.anima.features.event.repositories

import com.anima.features.event.entities.EventImageEntity

interface EventImageRepository {
    fun save(image: EventImageEntity): EventImageEntity
}
