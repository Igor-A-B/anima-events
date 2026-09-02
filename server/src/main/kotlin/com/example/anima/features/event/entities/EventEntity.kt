package com.example.anima.features.event.entities

import com.example.anima.features.address.entities.AddressEntity
import com.example.anima.features.exhibitor.entities.ExhibitorEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "Events")
class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @Column(length = 100, nullable = false)
    var description: String = ""

    @Lob
    @Column(length = 3000, nullable = false)
    var detail: String = ""

    @Column(nullable = false)
    var dateTime: LocalDateTime = LocalDateTime.now()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibitor_id", nullable = false)
    var exhibitor: ExhibitorEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    var address: AddressEntity? = null
}