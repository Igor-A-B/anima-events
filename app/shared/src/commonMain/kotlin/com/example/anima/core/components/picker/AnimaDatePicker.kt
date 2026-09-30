package com.example.anima.core.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AnimaDatePicker(
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    forceShow: Boolean = false,
    onDismiss: () -> Unit = {},
)