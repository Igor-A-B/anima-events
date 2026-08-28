package com.example.anima.features.feed.data

import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.feed.domain.FeedSection
import com.example.anima.features.feed.domain.FeedSectionType
import kotlinx.coroutines.delay

// fake data source for UI development
class MockFeedRepository : FeedRepository {

    override suspend fun getSections(category: EventCategory?): List<FeedSection> {
        // fake network latency
        delay(600)

        return listOf(
            FeedSection(type = FeedSectionType.HAPPENING_NOW, events = happeningNow),
            FeedSection(type = FeedSectionType.NEARBY, events = nearby),
            FeedSection(type = FeedSectionType.RECOMMENDED, events = recommended),
            FeedSection(type = FeedSectionType.PARTICIPATING, events = participating),
        )
            .map { section ->
                if (category == null) section
                else section.copy(events = section.events.filter { it.category == category })
            }
            .filter { section -> section.events.isNotEmpty() }
    }

    // mock data

    private val happeningNow = listOf(
        Event(
            id = "e1",
            title = "Baile do Bloco 9 - Open Deck",
            category = EventCategory.PARTY,
            venue = "Galpao 9",
            city = "Sao Paulo",
            dateLabel = "Agora",
            timeLabel = "22:00",
            price = "R$ 30",
            distanceLabel = "1,2 km",
            attendees = 412,
            isLive = true,
            coverSeed = 0,
        ),
        Event(
            id = "e2",
            title = "Jam de improviso no Beco",
            category = EventCategory.MUSIC,
            venue = "Beco do Batman",
            city = "Sao Paulo",
            dateLabel = "Agora",
            timeLabel = "21:30",
            price = null,
            distanceLabel = "3,8 km",
            attendees = 87,
            isLive = true,
            coverSeed = 1,
        ),
        Event(
            id = "e3",
            title = "Palco livre: stand-up aberto",
            category = EventCategory.THEATER,
            venue = "Teatro Pequeno",
            city = "Sao Paulo",
            dateLabel = "Agora",
            timeLabel = "20:00",
            price = "R$ 25",
            distanceLabel = "6,1 km",
            attendees = 143,
            isLive = true,
            coverSeed = 2,
        ),
    )

    private val nearby = listOf(
        Event(
            id = "e4",
            title = "Feira vegana da Vila",
            category = EventCategory.CUISINE,
            venue = "Praca Benedito Calixto",
            city = "Sao Paulo",
            dateLabel = "Hoje",
            timeLabel = "18:00",
            price = null,
            distanceLabel = "900 m",
            attendees = 260,
            coverSeed = 3,
        ),
        Event(
            id = "e5",
            title = "Cine ao ar livre: Cidade de Deus",
            category = EventCategory.CINEMA,
            venue = "Parque Augusta",
            city = "Sao Paulo",
            dateLabel = "Amanha",
            timeLabel = "19:30",
            price = null,
            distanceLabel = "2,4 km",
            attendees = 531,
            coverSeed = 4,
        ),
        Event(
            id = "e6",
            title = "Pedal noturno zona oeste",
            category = EventCategory.SPORT,
            venue = "Ciclovia Faria Lima",
            city = "Sao Paulo",
            dateLabel = "Sex, 28",
            timeLabel = "20:30",
            price = null,
            distanceLabel = "4,5 km",
            attendees = 178,
            coverSeed = 5,
        ),
        Event(
            id = "e7",
            title = "Exposicao Luz e Ruido",
            category = EventCategory.ARTS,
            venue = "Galeria Norte",
            city = "Sao Paulo",
            dateLabel = "Sab, 29",
            timeLabel = "11:00",
            price = "R$ 20",
            distanceLabel = "7,9 km",
            attendees = 94,
            coverSeed = 0,
        ),
    )

    private val recommended = listOf(
        Event(
            id = "e8",
            title = "Anima Fest 2026 - dia 1",
            category = EventCategory.MUSIC,
            venue = "Arena Anhembi",
            city = "Sao Paulo",
            dateLabel = "Sex, 28",
            timeLabel = "16:00",
            price = "R$ 180",
            distanceLabel = "12 km",
            attendees = 4820,
            coverSeed = 1,
        ),
        Event(
            id = "e9",
            title = "Sarau da quebrada",
            category = EventCategory.ARTS,
            venue = "CEU Capao Redondo",
            city = "Sao Paulo",
            dateLabel = "Sab, 29",
            timeLabel = "17:00",
            price = null,
            distanceLabel = "18 km",
            attendees = 302,
            coverSeed = 2,
        ),
        Event(
            id = "e10",
            title = "Workshop de Kotlin Multiplatform",
            category = EventCategory.TECH,
            venue = "Hub Paulista",
            city = "Sao Paulo",
            dateLabel = "Dom, 30",
            timeLabel = "10:00",
            price = "R$ 60",
            distanceLabel = "5,2 km",
            attendees = 66,
            coverSeed = 3,
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
            distanceLabel = "8,7 km",
            attendees = 221,
            coverSeed = 4,
        ),
    )

    private val participating = listOf(
        Event(
            id = "e12",
            title = "Turne Eclipse - show unico",
            category = EventCategory.MUSIC,
            venue = "Espaco Unimed",
            city = "Sao Paulo",
            dateLabel = "Qua, 02",
            timeLabel = "21:00",
            price = "R$ 240",
            distanceLabel = "9,3 km",
            attendees = 9120,
            coverSeed = 5,
        ),
        Event(
            id = "e13",
            title = "Corrida Anima 10k",
            category = EventCategory.SPORT,
            venue = "Parque Ibirapuera",
            city = "Sao Paulo",
            dateLabel = "Dom, 06",
            timeLabel = "07:00",
            price = "R$ 90",
            distanceLabel = "6,8 km",
            attendees = 2740,
            coverSeed = 0,
        ),
        Event(
            id = "e14",
            title = "Festival de food trucks",
            category = EventCategory.CUISINE,
            venue = "Memorial da America Latina",
            city = "Sao Paulo",
            dateLabel = "Sab, 05",
            timeLabel = "12:00",
            price = "R$ 15",
            distanceLabel = "11 km",
            attendees = 3310,
            coverSeed = 1,
        ),
    )
}
