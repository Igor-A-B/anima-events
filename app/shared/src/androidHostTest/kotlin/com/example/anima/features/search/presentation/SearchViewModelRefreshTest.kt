package com.example.anima.features.search.presentation

import com.anima.features.event.models.EventCategory
import com.example.anima.core.error.AppError
import com.example.anima.core.events.EventChanges
import com.example.anima.features.search.data.SearchRepository
import com.example.anima.features.search.domain.SearchFilters
import com.example.anima.features.search.domain.SearchPage
import com.example.anima.testing.event
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
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

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelRefreshTest {

    private data class Call(val query: String, val filters: SearchFilters, val cursor: String?)

    private class FakeSearchRepository : SearchRepository {
        val calls = mutableListOf<Call>()
        var response: suspend (String) -> SearchPage = { SearchPage(emptyList()) }

        override suspend fun search(query: String, filters: SearchFilters, cursor: String?): SearchPage {
            calls += Call(query, filters, cursor)
            return response(query)
        }
    }

    private val repository = FakeSearchRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun SearchUiState.ids() = results.map { it.id }

    private fun TestScope.loadedViewModel(): SearchViewModel {
        repository.response = { SearchPage(listOf(event("old")), nextCursor = "c1") }
        val viewModel = SearchViewModel(repository, EventChanges())
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun refreshReplacesTheResultsWithoutTheLoader() = runTest {
        val viewModel = loadedViewModel()
        viewModel.onToggleCategory(EventCategory.MUSIC)
        advanceUntilIdle()

        val gate = CompletableDeferred<Unit>()
        repository.response = { gate.await(); SearchPage(listOf(event("new"))) }

        viewModel.refresh()
        runCurrent()

        viewModel.uiState.value.let {
            assertTrue(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals(listOf("old"), it.ids())
        }
        // same query and filters, right away
        assertEquals(Call("", SearchFilters(categories = setOf(EventCategory.MUSIC)), null), repository.calls.last())

        gate.complete(Unit)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals(listOf("new"), it.ids())
            assertNull(it.nextCursor)
            assertNull(it.error)
        }
    }

    @Test
    fun failingRefreshKeepsTheResults() = runTest {
        val viewModel = loadedViewModel()
        repository.response = { throw IllegalStateException("boom") }

        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals(listOf("old"), it.ids())
            assertEquals("c1", it.nextCursor)
            assertNull(it.error)
        }
    }

    @Test
    fun failingRefreshWithoutResultsShowsTheError() = runTest {
        repository.response = { SearchPage(emptyList()) }
        val viewModel = SearchViewModel(repository, EventChanges())
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEmpty)

        repository.response = { throw IllegalStateException("boom") }
        viewModel.refresh()
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertEquals(AppError.UNKNOWN, it.error)
        }
    }

    @Test
    fun successfulRefreshClearsAnError() = runTest {
        repository.response = { throw IllegalStateException("boom") }
        val viewModel = SearchViewModel(repository, EventChanges())
        advanceUntilIdle()
        assertEquals(AppError.UNKNOWN, viewModel.uiState.value.error)

        val gate = CompletableDeferred<Unit>()
        repository.response = { gate.await(); SearchPage(listOf(event("new"))) }
        viewModel.refresh()
        runCurrent()
        // the error stays until the refresh succeeds
        assertEquals(AppError.UNKNOWN, viewModel.uiState.value.error)
        gate.complete(Unit)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertNull(it.error)
            assertEquals(listOf("new"), it.ids())
        }
    }

    @Test
    fun typingDuringRefreshCancelsIt() = runTest {
        val viewModel = loadedViewModel()
        val never = CompletableDeferred<Unit>()
        repository.response = { never.await(); error("unreachable") }

        viewModel.refresh()
        runCurrent()
        assertTrue(viewModel.uiState.value.isRefreshing)

        repository.response = { query -> SearchPage(listOf(event(query))) }
        viewModel.onQueryChange("jazz")
        assertFalse(viewModel.uiState.value.isRefreshing)

        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertFalse(it.isLoading)
            assertEquals(listOf("jazz"), it.ids())
        }
    }

    @Test
    fun filterChangeDuringRefreshCancelsIt() = runTest {
        val viewModel = loadedViewModel()
        val never = CompletableDeferred<Unit>()
        repository.response = { never.await(); error("unreachable") }

        viewModel.refresh()
        runCurrent()

        repository.response = { SearchPage(listOf(event("music"))) }
        viewModel.onToggleCategory(EventCategory.MUSIC)
        advanceUntilIdle()

        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing)
            assertEquals(listOf("music"), it.ids())
        }
    }

    @Test
    fun refreshIsIgnoredWhileOneIsRunning() = runTest {
        val viewModel = loadedViewModel()
        val gate = CompletableDeferred<Unit>()
        repository.response = { gate.await(); SearchPage(listOf(event("new"))) }
        val before = repository.calls.size

        viewModel.refresh()
        runCurrent()
        viewModel.refresh()
        runCurrent()
        // no next page either, the cursor is about to be replaced
        viewModel.loadMore()
        runCurrent()

        assertEquals(before + 1, repository.calls.size)
        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun eventChangeDuringRefreshEndsItAndShowsLoader() = runTest {
        val changes = EventChanges()
        repository.response = { SearchPage(listOf(event("old")), nextCursor = "c1") }
        val viewModel = SearchViewModel(repository, changes)
        advanceUntilIdle()
        val gate = CompletableDeferred<Unit>()
        repository.response = { gate.await(); SearchPage(listOf(event("new"))) }
        viewModel.refresh()
        runCurrent()
        assertTrue(viewModel.uiState.value.isRefreshing)
        changes.notifyChanged()
        runCurrent()
        assertFalse(viewModel.uiState.value.isRefreshing)
        gate.complete(Unit)
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing); assertFalse(it.isLoading); assertEquals(listOf("new"), it.ids())
        }
    }

    @Test
    fun refreshDuringDebounceUsesNewQueryAndEnds() = runTest {
        val viewModel = loadedViewModel()
        repository.response = { q -> SearchPage(listOf(event("r-$q"))) }
        viewModel.onQueryChange("abc")
        runCurrent()
        viewModel.refresh()
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing); assertFalse(it.isLoading); assertEquals(listOf("r-abc"), it.ids())
        }
    }

    @Test
    fun refreshDuringLoadMoreDropsThePage() = runTest {
        val viewModel = loadedViewModel()
        val moreGate = CompletableDeferred<Unit>()
        repository.response = { moreGate.await(); SearchPage(listOf(event("more"))) }
        viewModel.loadMore()
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoadingMore)
        repository.response = { SearchPage(listOf(event("new"))) }
        viewModel.refresh()
        moreGate.complete(Unit)
        advanceUntilIdle()
        viewModel.uiState.value.let {
            assertFalse(it.isRefreshing); assertFalse(it.isLoadingMore); assertEquals(listOf("new"), it.ids())
        }
    }
}
