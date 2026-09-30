package com.example.anima.core.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AnimaTimePicker(
    value: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    forceShow: Boolean = false,
    onDismiss: () -> Unit = {},
)