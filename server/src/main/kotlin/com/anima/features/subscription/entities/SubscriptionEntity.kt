package com.anima.features.subscription.entities

import com.anima.features.event.entities.EventEntity
import com.anima.features.subscription.models.SubscriptionStatus
import com.anima.features.visitor.entities.VisitorEntity
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
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.util.UUID

// one row per visitor and event, later also holds payment and accessibility details
@Entity
@Table(
    name = "Subscriptions",
    uniqueConstraints = [UniqueConstraint(columnNames = ["visitor_id", "event_id"])],
)
class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id", nullable = false)
    var visitor: VisitorEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    var event: EventEntity? = null

    @Column(nullable = false)
    var createdTimestamp: Instant = Instant.now()

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SubscriptionStatus = SubscriptionStatus.CONFIRMED
}
