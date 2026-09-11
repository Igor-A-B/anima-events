package com.example.anima.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

// label above a surface card
@Composable
fun ProfileSection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
        ) {
            Text(
                text = title.uppercase(),
                style = AnimaTheme.typography.labelMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            trailing?.invoke()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AnimaTheme.shapes.large)
                .background(AnimaTheme.colors.surface),
            content = content,
        )
    }
}

@Composable
fun ProfileRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AnimaTheme.spacing.lg,
                vertical = AnimaTheme.spacing.md,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
        ) {
            Text(
                text = label,
                style = AnimaTheme.typography.labelMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = value,
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        trailing?.invoke()

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = AnimaTheme.typography.labelLarge,
                color = AnimaTheme.colors.primaryVariant,
                modifier = Modifier
                    .clip(AnimaTheme.shapes.full)
                    .clickable(onClick = onActionClick)
                    .padding(
                        horizontal = AnimaTheme.spacing.sm,
                        vertical = AnimaTheme.spacing.xs,
                    ),
            )
        }
    }
}

@Composable
fun ProfileDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AnimaTheme.spacing.lg)
            .height(1.dp)
            .background(AnimaTheme.colors.outline.copy(alpha = 0.25f)),
    )
}
