package com.example.anima.core.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.example.anima.core.log.AppLog
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

// FileKit asks for the CAMERA permission and owns the file provider the camera writes to
@Composable
actual fun rememberCameraCapture(
    onCaptured: (PickedImage) -> Unit,
    onFailed: () -> Unit,
): (() -> Unit)? {
    val scope = rememberCoroutineScope()
    val currentOnCaptured by rememberUpdatedState(onCaptured)
    val currentOnFailed by rememberUpdatedState(onFailed)

    val launcher = rememberCameraPickerLauncher { file ->
        if (file == null) return@rememberCameraPickerLauncher
        scope.launch {
            try {
                currentOnCaptured(PickedImage(file.readBytes(), "photo.jpg", "image/jpeg"))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLog.e("CameraCapture", "couldn't read the photo", e)
                currentOnFailed()
            }
        }
    }
    return {
        try {
            launcher.launch()
        } catch (e: Exception) {
            // no camera app, or the launch was refused
            AppLog.e("CameraCapture", "couldn't open the camera", e)
            currentOnFailed()
        }
    }
}
