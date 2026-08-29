package com.example.anima.features.feed.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.viewmodel.compose.viewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.feed_empty
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.feed.domain.FeedSection
import com.example.anima.features.feed.presentation.components.CategoryFilterRow
import com.example.anima.features.feed.presentation.components.FeedHeader
import com.example.anima.features.feed.presentation.components.FeedSectionRow
import org.jetbrains.compose.resources.stringResource

// feed entry point, called by appNavGraph
@Composable
fun FeedScreen(
    onNavigateToEvent: (String) -> Unit = {},
    viewModel: FeedViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onEventClick = { event -> onNavigateToEvent(event.id) },
        onSeeAllClick = { /* TODO: navegar para a listagem completa da secao */ },
    )
}

// stateless part: data in, lambdas out
@Composable
private fun FeedContent(
    uiState: FeedUiState,
    onCategorySelected: (EventCategory?) -> Unit,
    onEventClick: (Event) -> Unit,
    onSeeAllClick: (FeedSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimaScaffold(insets = AnimaScaffoldInsets.WithChrome) {
        Column(
            modifier = modifier
                .background(AnimaTheme.colors.background)
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

            Box(
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

                    uiState.error.isNotBlank() -> FeedMessage(
                        text = stringResource(Res.string.core_error_generic),
                        modifier = Modifier.align(Alignment.Center),
                    )

                    uiState.isEmpty -> FeedMessage(
                        text = stringResource(Res.string.feed_empty),
                        modifier = Modifier.align(Alignment.Center),
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = AnimaTheme.spacing.xxxl),
                        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
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
