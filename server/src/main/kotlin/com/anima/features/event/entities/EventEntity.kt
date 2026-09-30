package com.anima.features.event.entities

import com.anima.features.event.models.EventCategory
import com.anima.features.user.entities.UserEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "Events")
class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @Column(nullable = false)
    var title: String = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: EventCategory = EventCategory.PARTY

    // place name, e.g. "Galpao 9"
    @Column(nullable = false)
    var venue: String = ""

    // street and number of the venue
    var address: String? = null

    @Column(nullable = false)
    var city: String = ""

    @Column(nullable = false)
    var startsAt: LocalDateTime = LocalDateTime.now()

    @Column(length = 3000, nullable = false)
    var description: String = ""

    var price: Double? = null
    var capacity: Int? = null

    var latitude: Double? = null
    var longitude: Double? = null

    // cover photo, null until the exhibitor uploads one
    @Column(length = 2048)
    var imageUrl: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    var organizer: UserEntity? = null

    @Column(nullable = false, updatable = false)
    var createdTimestamp: Instant = Instant.now()

    @Column(nullable = false)
    var updatedTimestamp: Instant = Instant.now()

    @PreUpdate
    fun onUpdate() {
        updatedTimestamp = Instant.now()
    }
    // TODO: delete the files from storage when the event is deleted
    @OneToMany(mappedBy = "event", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("createdAt")
    @BatchSize(size = 50)
    var images: MutableList<EventImageEntity> = mutableListOf()
}
