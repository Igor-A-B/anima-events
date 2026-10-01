package com.anima.features.address.entities

import com.anima.features.exhibitor.entities.ExhibitorEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "Adresses")
class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @Column(length = 100)
    var description: String = ""

    @Column
    var latitude: BigDecimal = BigDecimal.ZERO

    @Column
    var longitude: BigDecimal = BigDecimal.ZERO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibitor_id", nullable = false)
    var exhibitor: ExhibitorEntity? = null
}