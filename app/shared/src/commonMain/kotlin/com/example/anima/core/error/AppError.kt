package com.example.anima.core.error

import com.example.anima.core.log.AppLog
import com.example.anima.core.network.ApiException

// what went wrong, in terms the ui cares about
enum class AppError { INVALID_CREDENTIALS, INCORRECT_PASSWORD, EMAIL_ALREADY_EXISTS, VALIDATION, FORBIDDEN, FILE_TOO_LARGE, NETWORK, UNKNOWN }

fun Throwable.toAppError(): AppError {
    if (this !is ApiException) {
        AppLog.e("AppError", "unexpected exception", this)
        return AppError.UNKNOWN
    }
    return when (status) {
        null -> AppError.NETWORK
        400 -> AppError.VALIDATION
        401 -> AppError.INVALID_CREDENTIALS
        // the password change maps its own 403 to INCORRECT_PASSWORD, see ProfileViewModel
        403 -> AppError.FORBIDDEN
        409 -> AppError.EMAIL_ALREADY_EXISTS
        413 -> AppError.FILE_TOO_LARGE
        else -> AppError.UNKNOWN
    }
}
