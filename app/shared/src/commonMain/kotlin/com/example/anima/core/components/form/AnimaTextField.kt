package com.example.anima.core.components.form

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

@Composable
fun AnimaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colors = rememberAnimaTextFieldColors(
        isError = isError,
        enabled = enabled,
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        textStyle = AnimaTheme.typography.bodyLarge.copy(color = colors.content),
        cursorBrush = SolidColor(AnimaTheme.colors.primary),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = true,
        decorationBox = { innerTextField ->
            AnimaTextFieldDecoration(
                innerTextField = innerTextField,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                value = value,
                lineColor = colors.line,
                placeholderColor = AnimaTheme.colors.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun rememberAnimaTextFieldColors(
    isError: Boolean,
    enabled: Boolean,
): AnimaTextFieldColors {
    val line by animateColorAsState(
        targetValue = when {
            isError -> AnimaTheme.colors.error
            else -> AnimaTheme.colors.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "line",
    )

    val content by animateColorAsState(
        targetValue = when {
            !enabled -> AnimaTheme.colors.onSurfaceVariant
            isError -> AnimaTheme.colors.error
            else -> AnimaTheme.colors.onBackground
        },
        animationSpec = tween(200),
        label = "content",
    )

    val placeholder by animateColorAsState(
        targetValue = when {
            isError -> AnimaTheme.colors.error
            else -> AnimaTheme.colors.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "placeholder",
    )

    return AnimaTextFieldColors(line, content, placeholder)
}

private data class AnimaTextFieldColors(
    val line: Color,
    val content: Color,
    val placeholder: Color,
)

@Composable
private fun AnimaTextFieldDecoration(
    innerTextField: @Composable () -> Unit,
    value: String,
    lineColor: Color,
    placeholderColor: Color,
    placeholder: String?,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AnimaTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        ) {
            if (leadingIcon != null) {
                Box(contentAlignment = Alignment.Center) {
                    leadingIcon()
                }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = AnimaTheme.typography.bodyLarge,
                        color = placeholderColor,
                    )
                }
                innerTextField()
            }

            if (trailingIcon != null) {
                Box(contentAlignment = Alignment.Center) {
                    trailingIcon()
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = lineColor,
        )
    }
}