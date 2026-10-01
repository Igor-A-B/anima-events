package com.example.anima.core.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

enum class AnimaButtonVariant { PRIMARY, OUTLINED }

enum class AnimaButtonSize { DEFAULT, COMPACT }

@Composable
fun AnimaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AnimaButtonVariant = AnimaButtonVariant.PRIMARY,
    size: AnimaButtonSize = AnimaButtonSize.DEFAULT,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = rememberAnimaButtonColors(variant = variant)

    val contentPadding = PaddingValues(horizontal = AnimaTheme.spacing.xl)

    val shape = AnimaTheme.shapes.full

    val height = when (size) {
        AnimaButtonSize.DEFAULT -> AnimaButtonDefaults.Height
        AnimaButtonSize.COMPACT -> AnimaButtonDefaults.CompactHeight
    }

    val textStyle = when (size) {
        AnimaButtonSize.DEFAULT -> AnimaTheme.typography.titleMedium
        AnimaButtonSize.COMPACT -> AnimaTheme.typography.titleSmall
    }

    val indicatorSize = when (size) {
        AnimaButtonSize.DEFAULT -> AnimaButtonDefaults.IndicatorSize
        AnimaButtonSize.COMPACT -> AnimaButtonDefaults.CompactIndicatorSize
    }

    val clickEnabled = enabled && !loading

    val buttonModifier = modifier
        .fillMaxWidth()
        .height(height)

    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(indicatorSize),
                color = colors.content,
                strokeWidth = AnimaButtonDefaults.IndicatorStrokeWidth,
            )
        } else {
            Text(text = text, style = textStyle)
        }
    }


    when (variant) {
        AnimaButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = clickEnabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.background,
                    contentColor = colors.content,
                    disabledContainerColor = colors.background.copy(alpha = if (loading) 1f else 0.4f),
                    disabledContentColor = colors.content.copy(alpha = if (loading) 1f else 0.4f),
                ),
                contentPadding = contentPadding,
            ) {
                content()
            }
        }

        AnimaButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = clickEnabled,
                shape = shape,
                // solid, not transparent: the button floats over scrolling content (event detail footer)
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AnimaTheme.colors.background,
                    contentColor = colors.content,
                    disabledContainerColor = AnimaTheme.colors.background,
                    disabledContentColor = colors.content.copy(alpha = if (loading) 1f else 0.4f),
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = colors.content.copy(alpha = if (enabled || loading) 1f else 0.4f),
                ),
                contentPadding = contentPadding,
            ) {
                content()
            }
        }
    }
}

@Composable
private fun rememberAnimaButtonColors(
    variant: AnimaButtonVariant,
): AnimaButtonColors {
    val background by animateColorAsState(
        targetValue = when (variant) {
            AnimaButtonVariant.PRIMARY -> AnimaTheme.colors.primary
            AnimaButtonVariant.OUTLINED -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "ButtonBackground",
    )

    val content by animateColorAsState(
        targetValue = when (variant) {
            AnimaButtonVariant.PRIMARY -> AnimaTheme.colors.onPrimary
            AnimaButtonVariant.OUTLINED -> AnimaTheme.colors.primary
        },
        animationSpec = tween(200),
        label = "ButtonContent",
    )

    return AnimaButtonColors(background, content)
}

private data class AnimaButtonColors(
    val background: Color,
    val content: Color,
)

object AnimaButtonDefaults {
    val Height = 54.dp
    val CompactHeight = 36.dp

    val IndicatorSize: Dp = 24.dp
    val CompactIndicatorSize: Dp = 18.dp
    val IndicatorStrokeWidth: Dp = 2.dp
}