package com.example.anima.core.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AnimaDatePicker(
    value: String,
    onDateSelected: (String) -> Unit,
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