package com.anima.config

import com.google.cloud.storage.StorageException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class UploadExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    // multipart parsing fails before a route is picked, so the routes' own handlers never see it
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleTooLarge(): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(mapOf("error" to "file is too large (max 10MB)"))

    // unhandled errors end up as 401 (the /error forward is not authenticated), so answer here
    @ExceptionHandler(StorageException::class)
    fun handleStorage(ex: StorageException): ResponseEntity<Map<String, String>> {
        log.error("storage request failed", ex)
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(mapOf("error" to "storage is unavailable"))
    }
}
