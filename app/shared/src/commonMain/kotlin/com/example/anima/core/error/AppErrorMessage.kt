package com.example.anima.core.error

import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_conflict
import anima.app.shared.generated.resources.core_error_event_finished
import anima.app.shared.generated.resources.core_error_event_full
import anima.app.shared.generated.resources.core_error_event_not_found
import anima.app.shared.generated.resources.core_error_exhibitor_only
import anima.app.shared.generated.resources.core_error_file_too_large
import anima.app.shared.generated.resources.core_error_forbidden
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.core_error_image_unsupported
import anima.app.shared.generated.resources.core_error_image_upload_failed
import anima.app.shared.generated.resources.core_error_network
import anima.app.shared.generated.resources.core_error_not_found
import anima.app.shared.generated.resources.core_error_not_organizer
import anima.app.shared.generated.resources.core_error_server
import anima.app.shared.generated.resources.core_error_service_unavailable
import anima.app.shared.generated.resources.core_error_session_expired
import anima.app.shared.generated.resources.core_error_timeout
import anima.app.shared.generated.resources.core_error_too_many_requests
import anima.app.shared.generated.resources.core_error_validation
import anima.app.shared.generated.resources.core_error_visitor_only
import anima.app.shared.generated.resources.login_error_invalid_credentials
import anima.app.shared.generated.resources.profile_password_error_incorrect
import anima.app.shared.generated.resources.register_error_email_taken
import org.jetbrains.compose.resources.StringResource

// the text for an error, shared by inline messages and the snackbar
// core_error_generic ("unexpected error") is only for UNKNOWN
fun AppError.messageRes(): StringResource = when (this) {
    AppError.INVALID_CREDENTIALS -> Res.string.login_error_invalid_credentials
    AppError.INCORRECT_PASSWORD -> Res.string.profile_password_error_incorrect
    AppError.EMAIL_ALREADY_EXISTS -> Res.string.register_error_email_taken
    AppError.VALIDATION -> Res.string.core_error_validation
    AppError.SESSION_EXPIRED -> Res.string.core_error_session_expired
    AppError.FORBIDDEN -> Res.string.core_error_forbidden
    AppError.EXHIBITOR_ONLY -> Res.string.core_error_exhibitor_only
    AppError.VISITOR_ONLY -> Res.string.core_error_visitor_only
    AppError.NOT_ORGANIZER -> Res.string.core_error_not_organizer
    AppError.NOT_FOUND -> Res.string.core_error_not_found
    AppError.EVENT_NOT_FOUND -> Res.string.core_error_event_not_found
    AppError.EVENT_FULL -> Res.string.core_error_event_full
    AppError.EVENT_FINISHED -> Res.string.core_error_event_finished
    AppError.CONFLICT -> Res.string.core_error_conflict
    AppError.FILE_TOO_LARGE -> Res.string.core_error_file_too_large
    AppError.IMAGE_UPLOAD_FAILED -> Res.string.core_error_image_upload_failed
    AppError.IMAGE_UNSUPPORTED -> Res.string.core_error_image_unsupported
    AppError.TOO_MANY_REQUESTS -> Res.string.core_error_too_many_requests
    AppError.SERVER -> Res.string.core_error_server
    AppError.SERVICE_UNAVAILABLE -> Res.string.core_error_service_unavailable
    AppError.NETWORK -> Res.string.core_error_network
    AppError.TIMEOUT -> Res.string.core_error_timeout
    AppError.UNKNOWN -> Res.string.core_error_generic
}
