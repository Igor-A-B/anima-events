package com.example.anima.core.components.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

@Composable
internal fun AnimaPickerField(
    value: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val contentColor = if (value.isEmpty())
        AnimaTheme.colors.onSurfaceVariant
    else
        AnimaTheme.colors.onBackground

    val lineColor = AnimaTheme.colors.onSurfaceVariant
        .copy(alpha = if (enabled) 1f else 0.4f)

    Column(modifier = modifier.fillMaxWidth()) {
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

            Text(
                text = if (value.isEmpty()) placeholder.orEmpty() else value,
                style = AnimaTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f),
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = lineColor,
        )
    }
}