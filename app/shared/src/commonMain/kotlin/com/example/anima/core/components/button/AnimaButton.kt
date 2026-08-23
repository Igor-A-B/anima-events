package com.example.anima.core.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    when (variant) {
        AnimaButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.fillMaxWidth().height(height),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.background,
                    contentColor = colors.content,
                    disabledContainerColor = colors.background.copy(alpha = 0.4f),
                    disabledContentColor = colors.content.copy(alpha = 0.4f),
                ),
                contentPadding = contentPadding,
            ) {
                Text(text = text, style = textStyle)
            }
        }

        AnimaButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.content,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = colors.content.copy(alpha = 0.4f),
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = colors.content.copy(alpha = if (enabled) 1f else 0.4f),
                ),
                contentPadding = contentPadding,
            ) {
                Text(text = text, style = textStyle)
            }
        }
    }
}

@Composable
private fun rememberAnimaButtonColors(
    variant: AnimaButtonVariant,
    // enabled: Boolean,
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
}