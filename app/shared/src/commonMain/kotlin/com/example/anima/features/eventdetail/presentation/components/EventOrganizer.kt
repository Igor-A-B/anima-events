package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_organized_by
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCircleUser
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventOrganizer(
    organizerName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AnimaTheme.colors.surface,
                shape = AnimaTheme.shapes.medium,
            )
            .padding(AnimaTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(AnimaTheme.shapes.full)
                .background(AnimaTheme.colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            AnimaIcon(
                imageVector = LucideCircleUser,
                contentDescription = null,
                tint = AnimaTheme.colors.primary,
                size = 40.dp,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
        ) {
            Text(
                text = stringResource(Res.string.event_detail_organized_by),
                style = AnimaTheme.typography.labelSmall,
                color = AnimaTheme.colors.onSurfaceVariant,
            )

            Text(
                text = organizerName,
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurface,
            )
        }
    }
}