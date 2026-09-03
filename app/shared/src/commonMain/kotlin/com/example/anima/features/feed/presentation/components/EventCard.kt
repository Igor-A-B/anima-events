package com.example.anima.features.feed.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event

// event card used in feed carousels
@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(EventCardDefaults.Width)
            .clip(AnimaTheme.shapes.large)
            .background(AnimaTheme.colors.surface)
            .clickable(onClick = onClick),
    ) {
        EventCover(
            event = event,
            height = EventCardDefaults.CoverHeight,
        )

        // local and distance share one line: the place takes the space that is left
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AnimaTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${event.venue} - ${event.city}",
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

object EventCardDefaults {
    val Width = 232.dp
    val CoverHeight = 168.dp
}
