package com.example.anima.core.error

import org.jetbrains.compose.resources.StringResource

// what the global snackbar listens to: the text to show and how severe it is
class AppException(
    val messageRes: StringResource,
    val severity: Severity = Severity.ERROR,
    cause: Throwable? = null,
) : Exception(cause?.message, cause)

fun AppError.toAppException(severity: Severity = Severity.ERROR, cause: Throwable? = null) =
    AppException(messageRes(), severity, cause)

// an AppException passes through as is, anything else is mapped from its cause
fun Throwable.toAppException(): AppException =
    this as? AppException ?: toAppError().toAppException(cause = this)
