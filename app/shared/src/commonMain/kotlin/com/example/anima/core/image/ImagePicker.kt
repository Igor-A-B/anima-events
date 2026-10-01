package com.example.anima.core.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.example.anima.core.log.AppLog
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

// opens the platform image picker, returns the function that launches it
// onPicked gets jpeg, png and webp images; onUnsupported anything else, or a file that couldn't be read
// cancelling the picker calls neither
@Composable
fun rememberImagePicker(
    onPicked: (PickedImage) -> Unit,
    onUnsupported: () -> Unit = {},
): () -> Unit {
    val scope = rememberCoroutineScope()
    val currentOnPicked by rememberUpdatedState(onPicked)
    val currentOnUnsupported by rememberUpdatedState(onUnsupported)

    val launcher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file == null) return@rememberFilePickerLauncher
        val name = file.name
        scope.launch {
            try {
                val bytes = file.readBytes()
                val byName = PickedImage.mimeTypeOf(name)
                if (byName != null) {
                    currentOnPicked(PickedImage(bytes, name, byName))
                    return@launch
                }
                // no usable extension (content uris), trust the content instead
                val sniffed = PickedImage.sniff(bytes)
                if (sniffed == null) {
                    AppLog.i("ImagePicker", "unsupported image type: $name")
                    currentOnUnsupported()
                    return@launch
                }
                currentOnPicked(PickedImage(bytes, "photo.${sniffed.first}", sniffed.second))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLog.e("ImagePicker", "couldn't read $name", e)
                currentOnUnsupported()
            }
        }
    }
    return launcher::launch
}
