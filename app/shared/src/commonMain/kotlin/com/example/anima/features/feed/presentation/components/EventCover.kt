package com.example.anima.features.feed.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_interested
import anima.app.shared.generated.resources.feed_live
import anima.app.shared.generated.resources.feed_price_free
import anima.app.shared.generated.resources.feed_status_finished
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideUsers
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

// how much room the cover has for its chips
enum class EventCoverSize { DEFAULT, COMPACT }

// cover holds the date on top and the title at the bottom, over the gradient
@Composable
fun EventCover(
    event: Event,
    height: Dp,
    modifier: Modifier = Modifier,
    size: EventCoverSize = EventCoverSize.DEFAULT,
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
                // fill = false keeps SpaceBetween working: the chip takes only what
                // it needs, and gives way to the date chip when the row gets tight
                modifier = Modifier
                    .weight(1f, fill = false)
                    // reserved outside the chip, so the two never touch
                    .padding(end = AnimaTheme.spacing.sm),
            )

            // same slot, three meanings: when it happens, or that it is on, or over
            when (event.status) {
                // narrow covers drop the time: the day alone still tells you when
                EventStatus.UPCOMING -> EventChip(
                    text = when (size) {
                        EventCoverSize.DEFAULT -> "${event.dateLabel} - ${event.timeLabel}"
                        EventCoverSize.COMPACT -> event.dateLabel
                    },
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
                minLines = 1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
            ) {
                EventChip(
                    text = event.attendees.toString(),
                    background = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White,
                    leadingIcon = {
                        AnimaIcon(
                            imageVector = LucideUsers,
                            // only the number shows, the label goes to screen readers
                            contentDescription = stringResource(
                                Res.string.feed_interested,
                                event.attendees.toString(),
                            ),
                            size = EventChipDefaults.IconSize,
                        )
                    },
                )

                EventChip(
                    text = event.price ?: stringResource(Res.string.feed_price_free),
                    background = AnimaTheme.colors.surface,
                    contentColor = AnimaTheme.colors.onSurface,
                )
            }
        }
    }
}

object EventChipDefaults {
    val IconSize: Dp = 12.dp
}

@Composable
fun EventChip(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clip(AnimaTheme.shapes.full)
            .background(background)
            .padding(horizontal = AnimaTheme.spacing.sm, vertical = AnimaTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // the icon inherits the chip content color, so the caller passes no tint
        leadingIcon?.let { icon ->
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                content = icon,
            )
        }

        Text(
            text = text,
            style = AnimaTheme.typography.labelExtraSmall,
            color = contentColor,
            // a chip is a single line by definition, it shrinks instead of wrapping
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
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
            style = AnimaTheme.typography.labelExtraSmall,
            color = AnimaTheme.colors.onPrimary,
        )
    }
}
