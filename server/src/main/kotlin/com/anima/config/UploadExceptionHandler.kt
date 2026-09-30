package com.anima.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

// multipart parsing fails before a route is picked, so the routes' own handlers never see it
@RestControllerAdvice
class UploadExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleTooLarge(): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(mapOf("error" to "file is too large (max 10MB)"))
}
