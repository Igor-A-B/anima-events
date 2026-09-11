package com.example.anima.features.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.presentation.components.EventCover
import com.example.anima.features.feed.presentation.components.EventCoverSize

/** Grid cell for a search result */
@Composable
fun SearchResultCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AnimaTheme.shapes.large)
            .background(AnimaTheme.colors.surface)
            .clickable(onClick = onClick),
    ) {
        EventCover(
            event = event,
            height = SearchResultCardDefaults.CoverHeight,
            size = EventCoverSize.COMPACT,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AnimaTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = event.venue,
                style = AnimaTheme.typography.bodySmall,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            event.distanceLabel?.let { distance ->
                Text(
                    text = distance,
                    style = AnimaTheme.typography.labelSmall,
                    color = AnimaTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

object SearchResultCardDefaults {
    val CoverHeight: Dp = 132.dp
}
