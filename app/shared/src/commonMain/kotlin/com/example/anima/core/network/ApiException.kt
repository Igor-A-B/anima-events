package com.example.anima.core.network

// status is null when the server was not reached at all, isTimeout tells a timeout apart from no connection
// message is the server's {"error": "..."} text when it sent one
class ApiException(
    val status: Int?,
    message: String,
    val isTimeout: Boolean = false,
    cause: Throwable? = null,
) : Exception(message, cause)
