package com.anima.config

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anima.features.event.entities.EventEntity
import com.anima.features.event.models.EventCategory
import com.anima.features.event.repositories.EventRepository
import com.anima.features.exhibitor.entities.ExhibitorEntity
import com.anima.features.exhibitor.repositories.ExhibitorRepository
import com.anima.features.user.entities.UserEntity
import com.anima.features.user.repositories.UserRepository
import com.anima.features.visitor.entities.VisitorEntity
import com.anima.features.visitor.repositories.VisitorRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime

// dev only (SPRING_PROFILES_ACTIVE=dev): one visitor, one exhibitor and a few events, password is "anima123"
@Component
@Profile("dev")
class DevDataSeeder(
    private val users: UserRepository,
    private val visitors: VisitorRepository,
    private val exhibitors: ExhibitorRepository,
    private val events: EventRepository,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        if (!users.existsByEmail("visitor@anima.dev")) {
            val visitorUser = newUser("John Doe", "visitor@anima.dev")
            visitors.save(VisitorEntity().apply { user = visitorUser })
            val exhibitorUser = newUser("Galpao 9", "exhibitor@anima.dev")
            exhibitors.save(ExhibitorEntity().apply { user = exhibitorUser })
        }

        // checked on its own, so the events come back after server/db/recreate-events.sql
        val organizer = users.findByEmail("exhibitor@anima.dev").orElseThrow()
        if (events.findAllByOrganizerId(organizer.id!!).isNotEmpty()) return

        val now = LocalDateTime.now()
        listOf(
            Triple("Feira de Vinil", EventCategory.MUSIC, now.minusMinutes(30)),
            Triple("Noite de Jazz", EventCategory.MUSIC, now.plusHours(5)),
            Triple("Hackathon Anima", EventCategory.TECH, now.plusDays(1)),
            Triple("Festival de Cinema", EventCategory.CINEMA, now.plusDays(3)),
            Triple("Corrida no Parque", EventCategory.SPORT, now.minusDays(2)),
        ).forEachIndexed { i, (title, category, startsAt) ->
            events.save(EventEntity().apply {
                this.title = title
                this.category = category
                venue = "Galpao 9"
                address = "Rua Augusta, 900"
                city = "Sao Paulo"
                this.startsAt = startsAt
                description = "Evento de exemplo"
                latitude = -23.55 + i * 0.01
                longitude = -46.63 + i * 0.01
                this.organizer = organizer
            })
        }
    }

    private fun newUser(name: String, email: String) = users.save(UserEntity().apply {
        this.name = name
        this.email = email
        passwordHash = BCrypt.withDefaults().hashToString(10, "anima123".toCharArray())
    })
}
