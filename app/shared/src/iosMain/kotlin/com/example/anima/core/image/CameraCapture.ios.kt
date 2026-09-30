package com.example.anima.core.image

import androidx.compose.runtime.Composable

// no camera capture here, the picker falls back to the gallery only
@Composable
actual fun rememberCameraCapture(
    onCaptured: (PickedImage) -> Unit,
    onFailed: () -> Unit,
): (() -> Unit)? = null
