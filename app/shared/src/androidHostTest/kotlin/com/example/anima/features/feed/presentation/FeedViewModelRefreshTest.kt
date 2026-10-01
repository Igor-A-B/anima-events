package com.example.anima.features.feed.presentation

import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.FeedSectionType
import com.example.anima.core.events.EventChanges
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.feed.domain.FeedPage
import com.example.anima.testing.FakeSubscriptionRepository
import com.example.anima.testing.event
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class FeedViewModelRefreshTest {

    private class FakeFeedRepository : FeedRepository {
        var calls = 0
        var response: suspend (FeedSectionType, EventCategory?) -> FeedPage = { _, _ -> FeedPage(emptyList()) }

        override suspend fun getSection(type: FeedSectionType, category: EventCategory?, cursor: String?): FeedPage {
            calls++
            return response(type, category)
        }

        override suspend fun findById(id: String): Event? = null
    }

    private val repository = FakeFeedRepository()
    private val eventChanges = EventChanges()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun pageOf(id: String) = FeedPage(listOf(event(id)))

    private fun FeedUiState.ids() = sections.associate { it.type to it.events.map(Event::id) }

    private fun TestScope.loadedViewModel(id: String = "old"): FeedViewModel {
        repository.response = { type, _ -> pageOf("$id-${type.name}") }
        val viewModel = FeedViewModel(repository, FakeSubscriptionRepository(), eventChanges)
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun refreshReplacesTheSectionsWithoutTheLoader() = runTest {
        val viewModel = loadedViewModel()
        val gate = CompletableDeferred<Unit>()
        repository.response = { type, _ -> gate.await(); pageOf("new-${type.name}") }

        viewModel.refresh()
        runCurrent()

        viewModel.uiState.value.let {
            assertTrue(it.isRefreshing)
            assertFalse(it.isLoading)
            // the old feed stays on screen while it reloads
            assertEquals(FeedSectionType.entries.associateWith { type -> listOf("old-${type.name}") }, it.ids())
        }

        gate.complete(Unit)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals(FeedSectionType.entries.associateWith { type -> listOf("new-${type.name}") }, it.ids())
        }
    }

    @Test
    fun failingRefreshKeepsTheOldEvents() = runTest {
        val viewModel = loadedViewModel()
        val before = viewModel.uiState.value.sections
        repository.response = { type, _ ->
            if (type == FeedSectionType.entries.first()) throw IllegalStateException("boom") else pageOf("new-${type.name}")
        }

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertFalse(it.hasError)
            val first = FeedSectionType.entries.first()
            assertEquals(before.first { s -> s.type == first }, it.sections.first { s -> s.type == first })
            FeedSectionType.entries.drop(1).forEach { type ->
                assertEquals(listOf("new-${type.name}"), it.ids()[type])
            }
        }
    }

    @Test
    fun refreshingAnErroredFeedKeepsTheErrorUntilItLoads() = runTest {
        repository.response = { _, _ -> throw IllegalStateException("boom") }
        val viewModel = FeedViewModel(repository, FakeSubscriptionRepository(), eventChanges)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.hasError)

        val gate = CompletableDeferred<Unit>()
        repository.response = { type, _ -> gate.await(); pageOf("new-${type.name}") }
        viewModel.refresh()
        runCurrent()

        viewModel.uiState.value.let {
            assertTrue(it.isRefreshing)
            assertTrue(it.hasError)
            assertFalse(it.isLoading)
            assertFalse(it.isEmpty)
        }

        gate.complete(Unit)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.hasError)
            assertTrue(it.failed.isEmpty())
            assertEquals(FeedSectionType.entries.size, it.sections.size)
        }
    }

    @Test
    fun refreshingAnErroredFeedThatFailsAgainStaysErrored() = runTest {
        repository.response = { _, _ -> throw IllegalStateException("boom") }
        val viewModel = FeedViewModel(repository, FakeSubscriptionRepository(), eventChanges)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertTrue(it.hasError)
            assertFalse(it.isLoading)
        }
    }

    @Test
    fun refreshingAnEmptyFeedKeepsTheEmptyState() = runTest {
        repository.response = { _, _ -> FeedPage(emptyList()) }
        val viewModel = FeedViewModel(repository, FakeSubscriptionRepository(), eventChanges)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEmpty)

        val gate = CompletableDeferred<Unit>()
        repository.response = { _, _ -> gate.await(); FeedPage(emptyList()) }
        viewModel.refresh()
        runCurrent()

        viewModel.uiState.value.let {
            assertTrue(it.isRefreshing)
            assertTrue(it.isEmpty)
            assertFalse(it.isLoading)
        }

        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun refreshIsIgnoredWhileOneIsRunning() = runTest {
        val viewModel = loadedViewModel()
        val gate = CompletableDeferred<Unit>()
        repository.response = { type, _ -> gate.await(); pageOf("new-${type.name}") }
        val before = repository.calls

        viewModel.refresh()
        runCurrent()
        viewModel.refresh()
        runCurrent()

        assertEquals(before + FeedSectionType.entries.size, repository.calls)
        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun categoryChangeDuringRefreshEndsIt() = runTest {
        val viewModel = loadedViewModel()
        val never = CompletableDeferred<Unit>()
        repository.response = { _, _ -> never.await(); error("unreachable") }

        viewModel.refresh()
        runCurrent()
        assertTrue(viewModel.uiState.value.isRefreshing)

        repository.response = { type, _ -> pageOf("cat-${type.name}") }
        viewModel.onCategorySelected(EventCategory.MUSIC)

        // the new category shows its own spinner, not the refresh indicator
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertTrue(it.isLoading)
        }

        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals(FeedSectionType.entries.associateWith { type -> listOf("cat-${type.name}") }, it.ids())
        }
    }

    @Test
    fun reloadThatSupersedesTheRefreshStillEndsIt() = runTest {
        val viewModel = loadedViewModel()
        val never = CompletableDeferred<Unit>()
        repository.response = { _, _ -> never.await(); error("unreachable") }

        viewModel.refresh()
        runCurrent()

        // an event was created elsewhere, the feed reloads every section and cancels the refresh
        repository.response = { type, _ -> pageOf("changed-${type.name}") }
        eventChanges.notifyChanged()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertTrue(it.pending.isEmpty())
            assertEquals(FeedSectionType.entries.associateWith { type -> listOf("changed-${type.name}") }, it.ids())
        }
    }


    @Test
    fun refreshDuringPartialInitialLoad() = runTest {
        val gates = FeedSectionType.entries.associateWith { CompletableDeferred<Unit>() }
        repository.response = { type, _ -> gates.getValue(type).await(); pageOf("old-${type.name}") }
        val viewModel = FeedViewModel(repository, FakeSubscriptionRepository(), eventChanges)
        runCurrent()
        gates.getValue(FeedSectionType.entries.first()).complete(Unit)
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        repository.response = { type, _ -> pageOf("new-${type.name}") }
        viewModel.refresh()
        runCurrent()
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertTrue(it.pending.isEmpty())
            assertEquals(FeedSectionType.entries.associateWith { type -> listOf("new-${type.name}") }, it.ids())
        }
    }

    @Test
    fun loadMoreBeforeRefreshIsCancelledAndDoesNotAppendOldPage() = runTest {
        repository.response = { type, _ -> FeedPage(listOf(event("old-${type.name}")), nextCursor = "c") }
        val viewModel = FeedViewModel(repository, FakeSubscriptionRepository(), eventChanges)
        advanceUntilIdle()
        val first = FeedSectionType.entries.first()
        val moreGate = CompletableDeferred<Unit>()
        repository.response = { type, _ -> moreGate.await(); pageOf("more-${type.name}") }
        viewModel.loadMore(first)
        runCurrent()
        repository.response = { type, _ -> pageOf("new-${type.name}") }
        viewModel.refresh()
        moreGate.complete(Unit)
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertTrue(it.loadingMore.isEmpty())
            assertEquals(listOf("new-${first.name}"), it.ids()[first])
        }
    }

}
