package com.example.anima.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_live
import anima.app.shared.generated.resources.feed_status_finished
import anima.app.shared.generated.resources.profile_event_published
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventStatus
import com.example.anima.features.feed.presentation.components.eventCoverBrush
import org.jetbrains.compose.resources.stringResource

// compact line for an event the exhibitor created
@Composable
fun ProfileEventRow(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AnimaTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(ProfileEventRowDefaults.ThumbSize)
                .clip(AnimaTheme.shapes.medium)
                .background(eventCoverBrush(event.coverSeed)),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
        ) {
            Text(
                text = event.title,
                style = AnimaTheme.typography.titleSmall,
                color = AnimaTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${event.dateLabel} - ${event.timeLabel}",
                style = AnimaTheme.typography.labelSmall,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
            )
        }

        EventStatusPill(status = event.status)
    }
}

@Composable
private fun EventStatusPill(
    status: EventStatus,
    modifier: Modifier = Modifier,
) {
    val background = when (status) {
        EventStatus.OCCURRING -> AnimaTheme.colors.primary
        else -> AnimaTheme.colors.surfaceVariant
    }

    val contentColor = when (status) {
        EventStatus.OCCURRING -> AnimaTheme.colors.onPrimary
        EventStatus.FINISHED -> AnimaTheme.colors.onSurfaceVariant
        EventStatus.UPCOMING -> AnimaTheme.colors.onSurface
    }

    val text = when (status) {
        EventStatus.OCCURRING -> stringResource(Res.string.feed_live)
        EventStatus.FINISHED -> stringResource(Res.string.feed_status_finished)
        EventStatus.UPCOMING -> stringResource(Res.string.profile_event_published)
    }

    Text(
        text = text.uppercase(),
        style = AnimaTheme.typography.labelSmall,
        color = contentColor,
        maxLines = 1,
        modifier = modifier
            .clip(AnimaTheme.shapes.full)
            .background(background)
            .padding(
                horizontal = AnimaTheme.spacing.sm,
                vertical = AnimaTheme.spacing.xs,
            ),
    )
}

object ProfileEventRowDefaults {
    val ThumbSize = 52.dp
}
