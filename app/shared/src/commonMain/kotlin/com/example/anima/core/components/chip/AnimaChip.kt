package com.example.anima.core.components.chip

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

@Composable
fun AnimaChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val colors = rememberAnimaChipColors(selected = selected)

    val alpha = if (enabled) 1f else 0.4f
    val shape = AnimaTheme.shapes.large

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AnimaChipDefaults.Height)
            .clip(shape)
            .background(colors.background.copy(alpha = colors.background.alpha * alpha))
            .border(
                border = BorderStroke(
                    width = AnimaChipDefaults.BorderWidth,
                    color = colors.border.copy(alpha = alpha),
                ),
                shape = shape,
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                horizontal = AnimaTheme.spacing.lg,
                vertical = AnimaTheme.spacing.sm,
            ),
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        leadingIcon?.let { icon ->
            CompositionLocalProvider(
                LocalContentColor provides colors.content.copy(alpha = alpha),
                content = icon,
            )
        }

        Text(
            text = text,
            style = AnimaTheme.typography.titleSmall,
            color = colors.content.copy(alpha = alpha),
        )
    }
}

@Composable
private fun rememberAnimaChipColors(selected: Boolean): AnimaChipColors {
    val background by animateColorAsState(
        targetValue = if (selected) AnimaTheme.colors.primary
        else AnimaTheme.colors.surface,
        animationSpec = tween(200),
        label = "ChipBackground",
    )

    val content by animateColorAsState(
        targetValue = if (selected) AnimaTheme.colors.onPrimary
        else AnimaTheme.colors.onSurface,
        animationSpec = tween(200),
        label = "ChipContent",
    )

    val border by animateColorAsState(
        targetValue = if (selected) AnimaTheme.colors.primary
        else AnimaTheme.colors.outline,
        animationSpec = tween(200),
        label = "ChipBorder",
    )

    return AnimaChipColors(background, content, border)
}

private data class AnimaChipColors(
    val background: Color,
    val content: Color,
    val border: Color,
)

object AnimaChipDefaults {
    val Height: Dp = 40.dp
    val BorderWidth: Dp = 1.dp
    val IconSize: Dp = 18.dp
}
