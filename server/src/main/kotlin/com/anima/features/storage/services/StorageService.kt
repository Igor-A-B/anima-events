package com.anima.features.storage.services

// object storage for user uploaded files; paths come from StoragePaths
interface StorageService {
    fun upload(path: String, bytes: ByteArray, contentType: String)
    fun delete(path: String)

    // url the app can load the file from
    fun publicUrl(path: String): String
}
