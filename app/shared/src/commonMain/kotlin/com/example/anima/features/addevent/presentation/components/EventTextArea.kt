package com.example.anima.features.addevent.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

@Composable
fun EventTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = AnimaTheme.typography.bodyMedium.copy(
            color = AnimaTheme.colors.onBackground,
        ),
        cursorBrush = SolidColor(AnimaTheme.colors.primary),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .background(
                        color = AnimaTheme.colors.surface,
                        shape = AnimaTheme.shapes.medium,
                    )
                    .padding(AnimaTheme.spacing.md),
                contentAlignment = Alignment.TopStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = AnimaTheme.typography.bodyMedium,
                        color = AnimaTheme.colors.onSurfaceVariant,
                    )
                }
                innerTextField()
            }
        },
    )
}