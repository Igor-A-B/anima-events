package com.example.anima.core.image

import androidx.compose.runtime.Composable

// returns the function that opens the camera to take a photo, or null where the platform has no camera capture
// onCaptured gets the photo; onFailed when it couldn't be read; cancelling calls neither
@Composable
expect fun rememberCameraCapture(
    onCaptured: (PickedImage) -> Unit,
    onFailed: () -> Unit = {},
): (() -> Unit)?
