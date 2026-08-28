package com.anima.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "Exhibitors")
class ExhibitorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null

    @OneToMany(
        mappedBy = "exhibitor",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        targetEntity = AddressEntity::class
    )
    var adresses: List<AddressEntity> = mutableListOf()
}