package com.example.anima.features.visitor.entities

import com.example.anima.features.user.entities.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "Visitors")
class VisitorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null
}