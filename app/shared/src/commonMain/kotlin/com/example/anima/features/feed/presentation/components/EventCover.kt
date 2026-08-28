package com.example.anima.features.feed.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_live
import anima.app.shared.generated.resources.feed_price_free
import anima.app.shared.generated.resources.feed_status_finished
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.EventStatus
import org.jetbrains.compose.resources.stringResource

// gradients for the cover image until an image loader is added
private val coverPalettes: List<List<Color>> = listOf(
    listOf(Color(0xFFBC1F4B), Color(0xFF4A0E22)),
    listOf(Color(0xFF7A2A8C), Color(0xFF241046)),
    listOf(Color(0xFFE8476A), Color(0xFF8C1D3A)),
    listOf(Color(0xFF1F6FBC), Color(0xFF102C4A)),
    listOf(Color(0xFF1FA98C), Color(0xFF0E3B33)),
    listOf(Color(0xFFD98A1F), Color(0xFF4A2F0E)),
)

fun eventCoverBrush(coverSeed: Int): Brush =
    Brush.linearGradient(coverPalettes[coverSeed.mod(coverPalettes.size)])

// cover holds the date on top and the title at the bottom, over the gradient
@Composable
fun EventCover(
    event: Event,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(eventCoverBrush(event.coverSeed)),
    ) {
        // scrim: keeps the title readable over any gradient
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.75f),
                    )
                ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(AnimaTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            EventChip(
                text = event.category.label().uppercase(),
                background = Color.Black.copy(alpha = 0.45f),
                contentColor = Color.White,
            )

            // same slot, three meanings: when it happens, or that it is on, or over
            when (event.status) {
                EventStatus.UPCOMING -> EventChip(
                    text = "${event.dateLabel} - ${event.timeLabel}",
                    background = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White,
                )

                EventStatus.OCCURRING -> LiveBadge()

                EventStatus.FINISHED -> EventChip(
                    text = stringResource(Res.string.feed_status_finished).uppercase(),
                    background = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White.copy(alpha = 0.7f),
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(AnimaTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = event.title,
                style = AnimaTheme.typography.titleSmall,
                color = Color.White,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            EventChip(
                text = event.price ?: stringResource(Res.string.feed_price_free),
                background = AnimaTheme.colors.surface,
                contentColor = AnimaTheme.colors.onSurface,
            )
        }
    }
}

@Composable
fun EventChip(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = AnimaTheme.typography.labelSmall,
        color = contentColor,
        modifier = modifier
            .clip(AnimaTheme.shapes.full)
            .background(background)
            .padding(horizontal = AnimaTheme.spacing.sm, vertical = AnimaTheme.spacing.xs),
    )
}

@Composable
private fun LiveBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(AnimaTheme.shapes.full)
            .background(AnimaTheme.colors.primary)
            .padding(horizontal = AnimaTheme.spacing.sm, vertical = AnimaTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(AnimaTheme.shapes.full)
                .background(AnimaTheme.colors.onPrimary),
        )
        Text(
            text = stringResource(Res.string.feed_live),
            style = AnimaTheme.typography.labelSmall,
            color = AnimaTheme.colors.onPrimary,
        )
    }
}
