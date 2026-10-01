package com.example.anima.features.eventdetail.presentation

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.EventImage
import com.anima.features.event.models.FeedSectionType
import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.image.PickedImage
import com.example.anima.features.eventdetail.data.EventImageRepository
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.domain.FeedPage
import com.example.anima.testing.FakeSubscriptionRepository
import com.example.anima.testing.event
import com.example.anima.testing.signedOutSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelRefreshTest {

    private class FakeFeedRepository : FeedRepository {
        val requested = mutableListOf<String>()
        var response: suspend (String) -> Event? = { id -> event(id).copy(title = "old") }

        override suspend fun getSection(type: FeedSectionType, category: EventCategory?, cursor: String?) =
            FeedPage(emptyList())

        override suspend fun findById(id: String): Event? {
            requested += id
            return response(id)
        }
    }

    private class UnusedImages : EventImageRepository {
        override suspend fun upload(eventId: String, image: PickedImage): EventImage = error("unused")
    }

    private val feed = FakeFeedRepository()
    private val subscriptions = FakeSubscriptionRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    // the ui state only runs while collected, like on screen
    private fun TestScope.loadedViewModel(): EventDetailViewModel {
        val viewModel = EventDetailViewModel(feed, subscriptions, signedOutSession(), UnusedImages(), AppExceptionBus())
        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.loadEvent("e1")
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun refreshReloadsTheSameEvent() = runTest {
        val viewModel = loadedViewModel()
        val gate = CompletableDeferred<Unit>()
        feed.response = { id -> gate.await(); event(id).copy(title = "new") }
        val subscriptionLoads = subscriptions.refreshCalls

        viewModel.refresh()
        runCurrent()

        viewModel.uiState.value.let {
            assertTrue(it.isRefreshing)
            assertEquals("old", it.event?.title)
        }

        gate.complete(Unit)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertEquals("new", it.event?.title)
        }
        assertEquals("e1", feed.requested.last())
        assertEquals(subscriptionLoads + 1, subscriptions.refreshCalls)
    }

    @Test
    fun failingRefreshKeepsTheEvent() = runTest {
        val viewModel = loadedViewModel()
        feed.response = { throw IllegalStateException("boom") }
        subscriptions.onRefresh = { throw IllegalStateException("boom") }

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertEquals("old", it.event?.title)
        }
    }

    @Test
    fun refreshIsIgnoredWhileOneIsRunning() = runTest {
        val viewModel = loadedViewModel()
        val gate = CompletableDeferred<Unit>()
        feed.response = { id -> gate.await(); event(id) }
        val before = feed.requested.size

        viewModel.refresh()
        runCurrent()
        viewModel.refresh()
        runCurrent()

        assertEquals(before + 1, feed.requested.size)
        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun refreshBeforeAnyLoadDoesNothing() = runTest {
        val viewModel = EventDetailViewModel(feed, subscriptions, signedOutSession(), UnusedImages(), AppExceptionBus())
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.refresh()
        advanceUntilIdle()

        assertTrue(feed.requested.isEmpty())
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

}
