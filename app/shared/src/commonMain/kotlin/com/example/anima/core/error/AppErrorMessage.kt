package com.example.anima.core.error

import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.core_error_network
import anima.app.shared.generated.resources.login_error_invalid_credentials
import anima.app.shared.generated.resources.register_error_email_taken
import anima.app.shared.generated.resources.register_error_validation
import org.jetbrains.compose.resources.StringResource

// the text for an error, shared by inline messages and the future snackbar
fun AppError.messageRes(): StringResource = when (this) {
    AppError.INVALID_CREDENTIALS -> Res.string.login_error_invalid_credentials
    AppError.EMAIL_ALREADY_EXISTS -> Res.string.register_error_email_taken
    AppError.VALIDATION -> Res.string.register_error_validation
    AppError.NETWORK -> Res.string.core_error_network
    AppError.UNKNOWN -> Res.string.core_error_generic
}
