package com.example.anima.core.image

// an image the user picked, ready to upload
class PickedImage(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
) {
    companion object {
        // the types the server accepts, keyed by file extension
        private val mimeTypes = mapOf(
            "jpg" to "image/jpeg",
            "jpeg" to "image/jpeg",
            "png" to "image/png",
            "webp" to "image/webp",
        )

        // null for anything that is not jpeg, png or webp (heic, gif, ...)
        fun mimeTypeOf(fileName: String): String? =
            mimeTypes[fileName.substringAfterLast('.', "").lowercase()]
    }
}
