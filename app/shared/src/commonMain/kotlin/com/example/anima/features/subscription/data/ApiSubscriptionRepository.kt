package com.example.anima.features.subscription.data

import com.anima.features.subscription.models.Subscription
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ApiSubscriptionRepository(private val client: HttpClient) : SubscriptionRepository {

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    override val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    override suspend fun refresh() {
        _subscriptions.value = client.get("subscriptions/me").body()
    }

    override suspend fun subscribe(eventId: String) {
        put(client.post("events/$eventId/subscription").body())
    }

    override suspend fun cancel(eventId: String) {
        put(client.delete("events/$eventId/subscription").body())
    }

    // the server answers with the updated row, no need to fetch the list again
    private fun put(updated: Subscription) {
        _subscriptions.update { list -> list.filter { it.event.id != updated.event.id } + updated }
    }
}
