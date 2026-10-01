package com.anima.features.storage.services

import org.springframework.web.multipart.MultipartFile

private val EXTENSIONS = mapOf(
    "image/jpeg" to "jpg",
    "image/png" to "png",
    "image/webp" to "webp",
)

// a validated image from a multipart request, shared by event images and profile photos
class ImageUpload private constructor(val bytes: ByteArray, val contentType: String, val extension: String) {
    companion object {
        fun from(file: MultipartFile): ImageUpload {
            require(!file.isEmpty) { "file is empty" }
            val contentType = file.contentType?.lowercase()
            val extension = requireNotNull(EXTENSIONS[contentType]) { "only jpeg, png and webp images are allowed" }
            return ImageUpload(file.bytes, contentType!!, extension)
        }
    }
}
