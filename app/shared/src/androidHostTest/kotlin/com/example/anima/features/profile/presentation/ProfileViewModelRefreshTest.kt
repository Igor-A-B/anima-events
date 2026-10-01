package com.example.anima.features.profile.presentation

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.dtos.UpdateEventRequestDto
import com.anima.features.event.models.Event
import com.example.anima.core.error.AppError
import com.example.anima.features.addevent.data.ExhibitorEventRepository
import com.example.anima.features.profile.domain.model.UserProfile
import com.example.anima.features.profile.domain.repository.ProfileRepository
import com.example.anima.testing.FakeSubscriptionRepository
import com.example.anima.testing.signedOutSession
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

// the session has no tokens, so the screen runs as a visitor and the events come from subscriptions
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelRefreshTest {

    private class FakeProfileRepository : ProfileRepository {
        var response: suspend () -> UserProfile = { profile("old") }
        override suspend fun getProfile(): UserProfile = response()
        override suspend fun changePassword(currentPassword: String, newPassword: String) = Unit
    }

    private class UnusedExhibitorEvents : ExhibitorEventRepository {
        override val myEvents = MutableStateFlow<List<Event>>(emptyList())
        override suspend fun refresh() = Unit
        override suspend fun create(request: CreateEventRequestDto): Event = error("unused")
        override suspend fun getForm(id: String): UpdateEventRequestDto = error("unused")
        override suspend fun update(id: String, request: UpdateEventRequestDto): Event = error("unused")
        override suspend fun delete(id: String) = Unit
    }

    private val profiles = FakeProfileRepository()
    private val subscriptions = FakeSubscriptionRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.loadedViewModel(): ProfileViewModel {
        val viewModel = ProfileViewModel(profiles, signedOutSession(), UnusedExhibitorEvents(), subscriptions)
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun refreshReloadsProfileAndEventsWithoutTheLoader() = runTest {
        val viewModel = loadedViewModel()
        val profileGate = CompletableDeferred<Unit>()
        val eventsGate = CompletableDeferred<Unit>()
        profiles.response = { profileGate.await(); profile("new") }
        subscriptions.onRefresh = { eventsGate.await() }
        val eventLoads = subscriptions.refreshCalls

        viewModel.refresh()
        runCurrent()

        viewModel.uiState.value.let {
            assertTrue(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals("old", it.profile?.name)
        }
        assertEquals(eventLoads + 1, subscriptions.refreshCalls)

        // still refreshing until both are done
        profileGate.complete(Unit)
        runCurrent()
        viewModel.uiState.value.let {
            assertEquals("new", it.profile?.name)
            assertTrue(it.isRefreshing)
        }

        eventsGate.complete(Unit)
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertFalse(it.isLoadingEvents)
        }
    }

    @Test
    fun failingRefreshKeepsTheProfileWithoutTheErrorScreen() = runTest {
        val viewModel = loadedViewModel()
        profiles.response = { throw IllegalStateException("boom") }
        subscriptions.onRefresh = { throw IllegalStateException("boom") }

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals("old", it.profile?.name)
            // the list stays, the failed reload is only an inline note above it
            assertEquals(AppError.UNKNOWN, it.error)
            assertNotNull(it.eventsError)
        }
    }

    @Test
    fun refreshFromTheErrorScreenLoadsTheProfile() = runTest {
        profiles.response = { throw IllegalStateException("boom") }
        val viewModel = loadedViewModel()
        assertEquals(AppError.UNKNOWN, viewModel.uiState.value.error)

        profiles.response = { profile("new") }
        viewModel.refresh()
        runCurrent()
        // no full screen loader, the error stays until the profile arrives
        assertFalse(viewModel.uiState.value.isLoading)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertNull(it.error)
            assertEquals("new", it.profile?.name)
        }
    }

    @Test
    fun refreshIsIgnoredWhileOneIsRunning() = runTest {
        val viewModel = loadedViewModel()
        val gate = CompletableDeferred<Unit>()
        subscriptions.onRefresh = { gate.await() }
        val eventLoads = subscriptions.refreshCalls

        viewModel.refresh()
        runCurrent()
        viewModel.refresh()
        runCurrent()

        assertEquals(eventLoads + 1, subscriptions.refreshCalls)
        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun retryThatSupersedesTheEventsLoadStillEndsTheRefresh() = runTest {
        val viewModel = loadedViewModel()
        val never = CompletableDeferred<Unit>()
        subscriptions.onRefresh = { never.await() }

        viewModel.refresh()
        runCurrent()

        subscriptions.onRefresh = {}
        viewModel.loadEvents()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoadingEvents)
        }
    }

    private companion object {
        fun profile(name: String) = UserProfile(name = name, email = "$name@mail.com", registerDate = "2026-01-01T00:00:00")
    }

    @Test
    fun refreshDuringInitialLoadEndsAndShowsProfile() = runTest {
        val gate = CompletableDeferred<Unit>()
        profiles.response = { gate.await(); profile("first") }
        val viewModel = ProfileViewModel(profiles, signedOutSession(), UnusedExhibitorEvents(), subscriptions)
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        profiles.response = { profile("new") }
        viewModel.refresh()
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing); assertFalse(it.isLoading); assertEquals("new", it.profile?.name)
        }
    }

    @Test
    fun failingRefreshFromErrorKeepsErrorAndEnds() = runTest {
        profiles.response = { throw IllegalStateException("boom") }
        val viewModel = loadedViewModel()
        viewModel.refresh()
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing); assertFalse(it.isLoading); assertNotNull(it.error)
        }
    }

}