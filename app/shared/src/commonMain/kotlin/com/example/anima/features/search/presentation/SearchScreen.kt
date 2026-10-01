package com.example.anima.features.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.search_empty
import anima.app.shared.generated.resources.search_results_count
import com.example.anima.core.components.AnimaPullToRefresh
import com.example.anima.core.components.AnimaRefreshableFill
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.theme.AnimaTheme
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.DateFilter
import com.anima.features.event.models.PriceFilter
import com.example.anima.features.search.presentation.components.SearchField
import com.example.anima.features.search.presentation.components.SearchFilterBar
import com.example.anima.features.search.presentation.components.SearchFiltersSheet
import com.example.anima.features.search.presentation.components.SearchResultCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.example.anima.navigation.bottomnav.AnimaBottomNavDefaults
import kotlinx.coroutines.flow.filter

// search entry point, called by appNavGraph
@Composable
fun SearchScreen(
    onNavigateToEvent: (String) -> Unit = {},
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::onClearQuery,
        onToggleCategory = viewModel::onToggleCategory,
        onOpenFilters = { viewModel.onFiltersSheetVisibilityChange(true) },
        onDismissFilters = { viewModel.onFiltersSheetVisibilityChange(false) },
        onPriceChange = viewModel::onPriceFilterChange,
        onDateChange = viewModel::onDateFilterChange,
        onClearFilters = viewModel::onClearFilters,
        onEventClick = { event -> onNavigateToEvent(event.id) },
        onLoadMore = viewModel::loadMore,
        onRefresh = viewModel::refresh,
    )
}

// stateless part: data in, lambdas out
@Composable
private fun SearchContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onToggleCategory: (EventCategory) -> Unit,
    onOpenFilters: () -> Unit,
    onDismissFilters: () -> Unit,
    onPriceChange: (PriceFilter) -> Unit,
    onDateChange: (DateFilter) -> Unit,
    onClearFilters: () -> Unit,
    onEventClick: (Event) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)

    // asks for the next page when the user gets close to the end of the grid
    LaunchedEffect(gridState, uiState.nextCursor, uiState.results.size) {
        if (uiState.nextCursor == null) return@LaunchedEffect
        snapshotFlow {
            val info = gridState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - LOAD_MORE_THRESHOLD
        }
            .filter { it }
            .collect { currentOnLoadMore() }
    }

    AnimaScaffold(insets = AnimaScaffoldInsets.WithChrome) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = AnimaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.lg),
        ) {
            SearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onClear = onClearQuery,
                modifier = Modifier.padding(horizontal = AnimaTheme.spacing.lg),
            )

            SearchFilterBar(
                selectedCategories = uiState.filters.categories,
                activeFilterCount = uiState.filters.activeCount,
                onToggleCategory = onToggleCategory,
                onOpenFilters = onOpenFilters,
            )

            if (!uiState.isPristine && uiState.results.isNotEmpty()) {
                Text(
                    text = stringResource(
                        Res.string.search_results_count,
                        uiState.resultCountLabel,
                    ),
                    style = AnimaTheme.typography.labelLarge,
                    color = AnimaTheme.colors.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = AnimaTheme.spacing.lg),
                )
            }

            AnimaPullToRefresh(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when {
                    // the messages scroll so they can be pulled too
                    uiState.error.isNotBlank() -> AnimaRefreshableFill {
                        SearchMessage(text = stringResource(Res.string.core_error_generic))
                    }

                    uiState.isEmpty -> AnimaRefreshableFill {
                        SearchMessage(text = stringResource(Res.string.search_empty))
                    }

                    // also the first load: an empty grid plus the indicator on top
                    else -> LazyVerticalGrid(
                        columns = GridCells.Fixed(SEARCH_GRID_COLUMNS),
                        state = gridState,
                        modifier = Modifier
                            .fillMaxSize()
                            // results fade while the next search runs, instead of vanishing
                            .alpha(if (uiState.isLoading) LOADING_CONTENT_ALPHA else 1f),
                        // the nav floats over the grid, so the last row scrolls clear of it
                        contentPadding = PaddingValues(
                            start = AnimaTheme.spacing.lg,
                            bottom = AnimaBottomNavDefaults.ContentReserve,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
                    ) {
                        items(
                            items = uiState.results,
                            key = { event -> event.id },
                        ) { event ->
                            SearchResultCard(
                                event = event,
                                onClick = { onEventClick(event) },
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AnimaTheme.spacing.md),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(color = AnimaTheme.colors.primary)
                                }
                            }
                        }
                    }
                }

                // every search shows it, not just the first one
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = AnimaTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }

        if (uiState.isFiltersSheetVisible) {
            SearchFiltersSheet(
                filters = uiState.filters,
                resultCount = uiState.resultCountLabel,
                onPriceChange = onPriceChange,
                onDateChange = onDateChange,
                onClearFilters = onClearFilters,
                onDismiss = onDismissFilters,
            )
        }
    }
}

@Composable
private fun SearchMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = AnimaTheme.typography.bodyMedium,
        color = AnimaTheme.colors.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(AnimaTheme.spacing.xl),
    )
}

private const val SEARCH_GRID_COLUMNS = 2
private const val LOADING_CONTENT_ALPHA = 0.35f
private const val LOAD_MORE_THRESHOLD = 4
