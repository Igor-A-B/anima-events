package com.anima.features.storage.services

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class FirebaseStorageService(
    private val storage: Storage,
    @Value($$"${storage.bucket}") private val bucket: String,
    @Value($$"${storage.public-base-url}") private val publicBaseUrl: String,
) : StorageService {

    override fun upload(path: String, bytes: ByteArray, contentType: String) {
        val info = BlobInfo.newBuilder(BlobId.of(bucket, path)).setContentType(contentType).build()
        storage.create(info, bytes)
    }

    override fun delete(path: String) {
        storage.delete(BlobId.of(bucket, path))
    }

    // firebase download url format, served by the emulator and by firebasestorage.googleapis.com;
    // reads are allowed by storage.rules
    override fun publicUrl(path: String): String {
        val encoded = URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20")
        return "${publicBaseUrl.trimEnd('/')}/v0/b/$bucket/o/$encoded?alt=media"
    }
}
