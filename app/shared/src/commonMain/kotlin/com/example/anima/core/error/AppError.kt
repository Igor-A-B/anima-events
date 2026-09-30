package com.example.anima.core.error

import com.example.anima.core.log.AppLog
import com.example.anima.core.network.ApiException

// what went wrong, in terms the ui cares about
enum class AppError { INVALID_CREDENTIALS, INCORRECT_PASSWORD, EMAIL_ALREADY_EXISTS, VALIDATION, NETWORK, UNKNOWN }

fun Throwable.toAppError(): AppError {
    if (this !is ApiException) {
        AppLog.e("AppError", "unexpected exception", this)
        return AppError.UNKNOWN
    }
    return when (status) {
        null -> AppError.NETWORK
        400 -> AppError.VALIDATION
        401 -> AppError.INVALID_CREDENTIALS
        403 -> AppError.INCORRECT_PASSWORD
        409 -> AppError.EMAIL_ALREADY_EXISTS
        else -> AppError.UNKNOWN
    }
}
