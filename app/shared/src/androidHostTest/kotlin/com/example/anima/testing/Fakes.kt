package com.example.anima.testing

import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.auth.dtos.TokenResponseDto
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.subscription.models.Subscription
import com.example.anima.features.auth.data.AuthRepository
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.auth.data.TokenStorage
import com.example.anima.features.subscription.data.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow

fun event(id: String) = Event(
    id = id,
    title = id,
    category = EventCategory.MUSIC,
    venue = "v",
    city = "c",
    dateLabel = "d",
    timeLabel = "t",
)

class FakeSubscriptionRepository : SubscriptionRepository {
    override val subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    var refreshCalls = 0
    var onRefresh: suspend () -> Unit = {}

    override suspend fun refresh() {
        refreshCalls++
        onRefresh()
    }

    override suspend fun subscribe(eventId: String) = Unit
    override suspend fun cancel(eventId: String) = Unit
}

// no tokens stored, so nobody is signed in
fun signedOutSession() = SessionRepository(
    auth = object : AuthRepository {
        override suspend fun login(request: LoginRequestDto): TokenResponseDto = error("unused")
        override suspend fun register(request: RegisterRequestDto): TokenResponseDto = error("unused")
        override suspend fun refresh(request: RefreshRequestDto): TokenResponseDto = error("unused")
        override suspend fun logout(request: RefreshRequestDto) = Unit
    },
    storage = object : TokenStorage {
        override fun load(): TokenResponseDto? = null
        override fun save(tokens: TokenResponseDto) = Unit
        override fun clear() = Unit
    },
)
