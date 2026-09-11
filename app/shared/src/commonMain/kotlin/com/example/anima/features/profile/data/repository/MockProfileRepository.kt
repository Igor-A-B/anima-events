package com.example.anima.features.profile.data.repository

import com.example.anima.features.auth.data.MockAccount
import com.example.anima.features.auth.data.MockSession
import com.example.anima.features.auth.presentation.register.AccountType
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.feed.domain.EventStatus
import com.example.anima.features.profile.domain.model.ExhibitorContact
import com.example.anima.features.profile.domain.model.UserProfile
import com.example.anima.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.delay

// fake data
// the account comes from whoever the login signed in as
class MockProfileRepository(
    private val account: MockAccount = MockSession.account.value,
) : ProfileRepository {

    override suspend fun getProfile(): UserProfile {
        // fake network latency
        delay(600)

        return when (account.accountType) {
            AccountType.EXHIBITOR -> exhibitor
            AccountType.VISITOR -> visitor
        }
    }

    override suspend fun getAllCreatedEvents(): List<Event> {
        delay(600)

        return createdEvents
    }

    private val exhibitor = UserProfile(
        id = "u1",
        name = "Galpao 9",
        email = "contato@galpao9.com.br",
        document = "12.345.678/0001-90",
        recoveryEmail = "mateus@gmail.com",
        accountType = AccountType.EXHIBITOR,
        isVerified = false,
        contact = ExhibitorContact(
            link = "wa.me/5551999998888",
            phone = "(51) 99999-8888",
        ),
        createdEvents = createdEvents.take(PREVIEW_EVENT_COUNT),
        createdEventCount = createdEvents.size,
    )

    private val visitor = UserProfile(
        id = "u2",
        name = "John Doe",
        email = "john.doe@gmail.com",
        document = "123.456.789-00",
        recoveryEmail = "john.doe.bkp@gmail.com",
        accountType = AccountType.VISITOR,
        isVerified = true,
    )

    private companion object {
        const val PREVIEW_EVENT_COUNT = 3

        val createdEvents = listOf(
            Event(
                id = "e1",
                title = "Baile do Bloco 9 - Open Deck",
                category = EventCategory.PARTY,
                venue = "Galpao 9",
                city = "Sao Paulo",
                dateLabel = "Hoje",
                timeLabel = "22:00",
                price = "R$ 30",
                attendees = 412,
                status = EventStatus.OCCURRING,
                coverSeed = 0,
            ),
            Event(
                id = "e11",
                title = "Noite de forro no quintal",
                category = EventCategory.PARTY,
                venue = "Quintal da Lapa",
                city = "Sao Paulo",
                dateLabel = "Dom, 30",
                timeLabel = "19:00",
                price = "R$ 35",
                attendees = 221,
                coverSeed = 4,
            ),
            Event(
                id = "e2",
                title = "Jam de improviso no Beco",
                category = EventCategory.MUSIC,
                venue = "Beco do Batman",
                city = "Sao Paulo",
                dateLabel = "Sab, 29",
                timeLabel = "21:30",
                attendees = 87,
                status = EventStatus.FINISHED,
                coverSeed = 1,
            ),
            Event(
                id = "e9",
                title = "Sarau da quebrada",
                category = EventCategory.ARTS,
                venue = "CEU Capao Redondo",
                city = "Sao Paulo",
                dateLabel = "Sab, 22",
                timeLabel = "17:00",
                attendees = 302,
                status = EventStatus.FINISHED,
                coverSeed = 2,
            ),
        )
    }
}
