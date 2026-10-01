package com.anima.features.event.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

// the file lives in storage, only its path is kept here
@Entity
@Table(name = "EventImages")
class EventImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    var event: EventEntity? = null

    @Column(nullable = false)
    var objectPath: String = ""

    @Column(nullable = false)
    var contentType: String = ""

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
}
