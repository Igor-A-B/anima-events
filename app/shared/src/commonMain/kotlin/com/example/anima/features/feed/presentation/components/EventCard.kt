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
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_interested
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideUsers
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import org.jetbrains.compose.resources.stringResource

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

        Column(
            modifier = Modifier.padding(AnimaTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
        ) {
            Text(
                text = "${event.venue} - ${event.city}",
                style = AnimaTheme.typography.bodySmall,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AnimaTheme.spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimaIcon(
                    imageVector = LucideUsers,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = 14.dp,
                )
                Text(
                    text = stringResource(Res.string.feed_interested, event.attendees.toString()),
                    style = AnimaTheme.typography.labelSmall,
                    color = AnimaTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
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
}

object EventCardDefaults {
    val Width = 232.dp
    val CoverHeight = 168.dp
}
