package com.example.anima.features.feed.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_see_all
import com.example.anima.core.theme.AnimaTheme
import com.anima.features.event.models.Event
import com.example.anima.features.feed.domain.FeedSection
import org.jetbrains.compose.resources.stringResource

private const val LOAD_MORE_THRESHOLD = 3
private val EventCardHeightHint = 120.dp

// section header and the horizontal carousel of cards
@Composable
fun FeedSectionRow(
    section: FeedSection,
    onEventClick: (Event) -> Unit,
    onSeeAllClick: (FeedSection) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)

    // asks for the next page when the user gets close to the end of the row
    LaunchedEffect(listState, section.nextCursor, section.events.size) {
        if (section.nextCursor == null) return@LaunchedEffect
        snapshotFlow {
            val info = listState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - LOAD_MORE_THRESHOLD
        }
            .filter { it }
            .collect { currentOnLoadMore() }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AnimaTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
            ) {
                Text(
                    text = section.type.title(),
                    style = AnimaTheme.typography.titleLarge,
                    color = AnimaTheme.colors.onBackground,
                )
            }

            Text(
                text = stringResource(Res.string.feed_see_all),
                style = AnimaTheme.typography.labelLarge,
                color = AnimaTheme.colors.primary,
                modifier = Modifier
                    .clip(AnimaTheme.shapes.full)
                    .clickable { onSeeAllClick(section) }
                    .padding(
                        horizontal = AnimaTheme.spacing.sm,
                        vertical = AnimaTheme.spacing.xs,
                    ),
            )
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = AnimaTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        ) {
            items(items = section.events, key = { event -> event.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                )
            }

            if (isLoadingMore) {
                item(key = "loading-more") {
                    Box(modifier = Modifier.height(EventCardHeightHint).padding(horizontal = AnimaTheme.spacing.lg), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AnimaTheme.colors.primary, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
