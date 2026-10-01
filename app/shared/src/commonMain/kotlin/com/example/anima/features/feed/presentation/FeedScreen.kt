package com.example.anima.features.feed.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_empty
import com.example.anima.core.components.AnimaPullToRefresh
import com.example.anima.core.components.AnimaRefreshableFill
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.error.AppError
import com.example.anima.core.error.messageRes
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.theme.AnimaTheme
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.FeedSectionType
import com.example.anima.features.feed.domain.FeedSection
import com.example.anima.features.feed.presentation.components.CategoryFilterRow
import com.example.anima.features.feed.presentation.components.FeedHeader
import com.example.anima.features.feed.presentation.components.FeedSectionRow
import org.jetbrains.compose.resources.stringResource
import com.example.anima.navigation.bottomnav.AnimaBottomNavDefaults

// feed entry point, called by appNavGraph
@Composable
fun FeedScreen(
    onNavigateToEvent: (String) -> Unit = {},
    viewModel: FeedViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onEventClick = { event -> onNavigateToEvent(event.id) },
        onSeeAllClick = { /* TODO: navegar para a listagem completa da secao */ },
        onLoadMore = viewModel::loadMore,
        onRefresh = viewModel::refresh,
    )
}

// stateless part: data in, lambdas out
@Composable
private fun FeedContent(
    uiState: FeedUiState,
    onCategorySelected: (EventCategory?) -> Unit,
    onEventClick: (Event) -> Unit,
    onSeeAllClick: (FeedSection) -> Unit,
    onLoadMore: (FeedSectionType) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimaScaffold(insets = AnimaScaffoldInsets.WithChrome) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .padding(top = AnimaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.lg),
        ) {
            // header and filter stay put, only the area below reacts to the state
            FeedHeader(modifier = Modifier.padding(horizontal = AnimaTheme.spacing.lg))

            CategoryFilterRow(
                selected = uiState.selectedCategory,
                onSelect = onCategorySelected,
            )

            AnimaPullToRefresh(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                // one explicit branch per state: loading, error, empty, content
                when {
                    uiState.isLoading -> CircularProgressIndicator(
                        color = AnimaTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    // the messages scroll so they can be pulled too
                    uiState.hasError -> AnimaRefreshableFill {
                        FeedMessage(text = stringResource((uiState.error ?: AppError.UNKNOWN).messageRes()))
                    }

                    uiState.isEmpty -> AnimaRefreshableFill {
                        FeedMessage(text = stringResource(Res.string.feed_empty))
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = AnimaBottomNavDefaults.ContentReserve),
                        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxxl),
                    ) {
                        // one FeedSectionRow per section
                        items(
                            items = uiState.sections,
                            key = { section -> section.type.name },
                        ) { section ->
                            FeedSectionRow(
                                section = section,
                                onEventClick = onEventClick,
                                onSeeAllClick = onSeeAllClick,
                                onLoadMore = { onLoadMore(section.type) },
                                isLoadingMore = section.type in uiState.loadingMore,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedMessage(
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
