package com.example.anima.core.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AnimaTimePicker(
    value: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier,
    placeholder: String?,
    enabled: Boolean,
    forceShow: Boolean,
    onDismiss: () -> Unit,
) {
    AnimaPickerField(
        value = value,
        placeholder = placeholder,
        enabled = enabled,
        modifier = modifier,
    )
}