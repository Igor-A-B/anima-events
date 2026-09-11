package com.example.anima.features.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.search_empty
import anima.app.shared.generated.resources.search_results_count
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.search.domain.DateFilter
import com.example.anima.features.search.domain.PriceFilter
import com.example.anima.features.search.presentation.components.SearchField
import com.example.anima.features.search.presentation.components.SearchFilterBar
import com.example.anima.features.search.presentation.components.SearchFiltersSheet
import com.example.anima.features.search.presentation.components.SearchResultCard
import org.jetbrains.compose.resources.stringResource

// search entry point, called by appNavGraph
@Composable
fun SearchScreen(
    onNavigateToEvent: (String) -> Unit = {},
    viewModel: SearchViewModel = viewModel(),
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
    modifier: Modifier = Modifier,
) {
    AnimaScaffold(insets = AnimaScaffoldInsets.WithChrome) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = AnimaTheme.spacing.lg)
                .padding(bottom = AnimaTheme.spacing.xxxl),
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
                        uiState.results.size.toString(),
                    ),
                    style = AnimaTheme.typography.labelLarge,
                    color = AnimaTheme.colors.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = AnimaTheme.spacing.lg),
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when {
                    uiState.error.isNotBlank() -> SearchMessage(
                        text = stringResource(Res.string.core_error_generic),
                        modifier = Modifier.align(Alignment.Center),
                    )

                    uiState.isEmpty -> SearchMessage(
                        text = stringResource(Res.string.search_empty),
                        modifier = Modifier.align(Alignment.Center),
                    )

                    // also the first load: an empty grid plus the indicator on top
                    else -> LazyVerticalGrid(
                        columns = GridCells.Fixed(SEARCH_GRID_COLUMNS),
                        modifier = Modifier
                            .fillMaxSize()
                            // results fade while the next search runs, instead of vanishing
                            .alpha(if (uiState.isLoading) LOADING_CONTENT_ALPHA else 1f),
                        contentPadding = PaddingValues(
                            start = AnimaTheme.spacing.lg,
                            end = AnimaTheme.spacing.lg,
                            bottom = AnimaTheme.spacing.xxxl,
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
                resultCount = uiState.results.size,
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
