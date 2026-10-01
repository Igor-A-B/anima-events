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

        // the type by the first bytes, for files whose name has no usable extension
        // returns (extension, mime type) or null when it is not jpeg, png or webp
        fun sniff(bytes: ByteArray): Pair<String, String>? {
            fun at(i: Int) = bytes.getOrNull(i)?.toInt()?.and(0xFF)
            return when {
                at(0) == 0xFF && at(1) == 0xD8 && at(2) == 0xFF -> "jpg" to "image/jpeg"
                at(0) == 0x89 && at(1) == 0x50 && at(2) == 0x4E && at(3) == 0x47 -> "png" to "image/png"
                bytes.size >= 12 &&
                    bytes.decodeToString(0, 4) == "RIFF" && bytes.decodeToString(8, 12) == "WEBP" -> "webp" to "image/webp"
                else -> null
            }
        }

        // null for anything that is not jpeg, png or webp (heic, gif, ...)
        fun mimeTypeOf(fileName: String): String? =
            mimeTypes[fileName.substringAfterLast('.', "").lowercase()]
    }
}
