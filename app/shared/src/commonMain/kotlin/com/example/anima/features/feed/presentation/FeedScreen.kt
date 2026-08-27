package com.example.anima.features.feed.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.FeedSection
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
        onEventClick = { event -> onNavigateToEvent(event.id) },
        onSeeAllClick = { /* TODO: navegar para a listagem completa da secao */ },
    )
}

// stateless part: data in, lambdas out
@Composable
private fun FeedContent(
    uiState: FeedUiState,
    onEventClick: (Event) -> Unit,
    onSeeAllClick: (FeedSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(AnimaTheme.colors.background)
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        // one explicit branch per state: loading, error, empty, content
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = AnimaTheme.colors.primary,
                modifier = Modifier.align(Alignment.Center),
            )
            return@Box
        }

        if (uiState.error.isNotBlank()) {
            FeedMessage(
                text = stringResource(Res.string.core_error_generic),
                modifier = Modifier.align(Alignment.Center),
            )
            return@Box
        }

        if (uiState.isEmpty) {
            FeedMessage(
                text = stringResource(Res.string.feed_empty),
                modifier = Modifier.align(Alignment.Center),
            )
            return@Box
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = AnimaTheme.spacing.lg,
                bottom = AnimaTheme.spacing.xxxl,
            ),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
        ) {
            item(key = "header") {
                FeedHeader(
                    modifier = Modifier.padding(horizontal = AnimaTheme.spacing.lg),
                )
            }

            // one FeedSectionRow per section
            items(items = uiState.sections, key = { section -> section.type.name }) { section ->
                FeedSectionRow(
                    section = section,
                    onEventClick = onEventClick,
                    onSeeAllClick = onSeeAllClick,
                )
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
