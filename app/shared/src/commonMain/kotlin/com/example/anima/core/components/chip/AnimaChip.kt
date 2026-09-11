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
    size: AnimaChipSize = AnimaChipSize.DEFAULT,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val colors = rememberAnimaChipColors(selected = selected)

    val alpha = if (enabled) 1f else 0.4f

    val shape = AnimaTheme.shapes.large

    val height = when (size) {
        AnimaChipSize.DEFAULT -> AnimaChipDefaults.Height
        AnimaChipSize.COMPACT -> AnimaChipDefaults.CompactHeight
    }

    val textStyle = when (size) {
        AnimaChipSize.DEFAULT -> AnimaTheme.typography.titleSmall
        AnimaChipSize.COMPACT -> AnimaTheme.typography.labelMedium
    }

    val horizontalPadding = when (size) {
        AnimaChipSize.DEFAULT -> AnimaTheme.spacing.lg
        AnimaChipSize.COMPACT -> AnimaTheme.spacing.md
    }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = height)
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
                horizontal = horizontalPadding,
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
            style = textStyle,
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

enum class AnimaChipSize { DEFAULT, COMPACT }

object AnimaChipDefaults {
    val Height: Dp = 40.dp
    val CompactHeight: Dp = 32.dp
    val BorderWidth: Dp = 1.dp
    val IconSize: Dp = 18.dp
    val CompactIconSize: Dp = 14.dp
}
