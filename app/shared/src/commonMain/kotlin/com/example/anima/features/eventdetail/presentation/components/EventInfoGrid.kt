package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_attendees
import anima.app.shared.generated.resources.event_detail_attendees_count
import anima.app.shared.generated.resources.event_detail_date
import anima.app.shared.generated.resources.event_detail_free
import anima.app.shared.generated.resources.event_detail_full
import anima.app.shared.generated.resources.event_detail_price
import anima.app.shared.generated.resources.event_detail_spots_left
import anima.app.shared.generated.resources.event_detail_unlimited
import anima.app.shared.generated.resources.event_detail_location
import anima.app.shared.generated.resources.event_detail_time
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCalendar
import com.example.anima.core.components.icon.lucide.LucideClock
import com.example.anima.core.components.icon.lucide.LucideMapPin
import com.example.anima.core.components.icon.lucide.LucideTicket
import com.example.anima.core.components.icon.lucide.LucideUsers
import com.example.anima.core.theme.AnimaTheme
import com.anima.features.event.models.Event
import com.example.anima.features.eventdetail.presentation.isFull
import com.example.anima.features.eventdetail.presentation.spotsLeft
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventInfoGrid(
    dateLabel: String,
    timeLabel: String,
    venue: String,
    city: String,
    distanceLabel: String?,
    // attendees, capacity (null means unlimited) and price (null means free)
    event: Event,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        ) {
            EventInfoCard(
                icon = LucideCalendar,
                label = stringResource(Res.string.event_detail_date),
                value = dateLabel,
                modifier = Modifier.weight(1f),
            )

            EventInfoCard(
                icon = LucideClock,
                label = stringResource(Res.string.event_detail_time),
                value = timeLabel,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        ) {
            EventInfoCard(
                icon = LucideMapPin,
                label = stringResource(Res.string.event_detail_location),
                value = venue,
                subValue = distanceLabel ?: city,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            val people = stringResource(Res.string.event_detail_attendees_count)
            val spotsLeft = event.spotsLeft()
            val isFull = event.isFull()
            EventInfoCard(
                icon = LucideUsers,
                label = stringResource(Res.string.event_detail_attendees),
                value = event.capacity?.let { "${event.attendees} / $it $people" } ?: "${event.attendees} $people",
                subValue = when {
                    spotsLeft == null -> stringResource(Res.string.event_detail_unlimited)
                    isFull -> stringResource(Res.string.event_detail_full)
                    else -> stringResource(Res.string.event_detail_spots_left, spotsLeft.toString())
                },
                subValueColor = if (isFull) AnimaTheme.colors.error else null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }

        EventInfoCard(
            icon = LucideTicket,
            label = stringResource(Res.string.event_detail_price),
            value = event.price ?: stringResource(Res.string.event_detail_free),
            valueColor = if (event.price == null) AnimaTheme.colors.success else null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EventInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    subValue: String? = null,
    valueColor: Color? = null,
    subValueColor: Color? = null,
) {
    Column(
        modifier = modifier
            .background(
                color = AnimaTheme.colors.surface,
                shape = AnimaTheme.shapes.medium,
            )
            .padding(AnimaTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
        ) {
            AnimaIcon(
                imageVector = icon,
                contentDescription = null,
                tint = AnimaTheme.colors.primary,
                size = AnimaTheme.spacing.lg,
            )

            Text(
                text = label,
                style = AnimaTheme.typography.labelSmall,
                color = AnimaTheme.colors.onSurfaceVariant,
            )
        }

        Text(
            text = value,
            style = AnimaTheme.typography.bodyMedium,
            color = valueColor ?: AnimaTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (subValue != null) {
            Text(
                text = subValue,
                style = AnimaTheme.typography.bodySmall,
                color = subValueColor ?: AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}