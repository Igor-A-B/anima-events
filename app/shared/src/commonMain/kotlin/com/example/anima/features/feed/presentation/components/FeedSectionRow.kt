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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_see_all
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.Event
import com.example.anima.features.feed.domain.FeedSection
import org.jetbrains.compose.resources.stringResource

// section header and the horizontal carousel of cards
@Composable
fun FeedSectionRow(
    section: FeedSection,
    onEventClick: (Event) -> Unit,
    onSeeAllClick: (FeedSection) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                Text(
                    text = section.type.subtitle(),
                    style = AnimaTheme.typography.bodySmall,
                    color = AnimaTheme.colors.onSurfaceVariant,
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
            contentPadding = PaddingValues(horizontal = AnimaTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        ) {
            items(items = section.events, key = { event -> event.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                )
            }
        }
    }
}
