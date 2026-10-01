package com.example.anima.core.error

import com.example.anima.core.log.AppLog
import com.example.anima.core.network.ApiException

// what went wrong, in terms the ui cares about
enum class AppError {
    // wrong email or password on login
    INVALID_CREDENTIALS,
    // wrong current password when changing it
    INCORRECT_PASSWORD,
    EMAIL_ALREADY_EXISTS,
    // the server rejected the data (400/422)
    VALIDATION,
    // a signed in call got a 401 the refresh could not fix, the user has to sign in again
    SESSION_EXPIRED,
    // a 403 with no more specific reason
    FORBIDDEN,
    EXHIBITOR_ONLY,
    VISITOR_ONLY,
    // only the organizer of the event can do that
    NOT_ORGANIZER,
    NOT_FOUND,
    EVENT_NOT_FOUND,
    EVENT_FULL,
    EVENT_FINISHED,
    // a 409 with no more specific reason
    CONFLICT,
    FILE_TOO_LARGE,
    IMAGE_UPLOAD_FAILED,
    IMAGE_LOAD_FAILED,
    IMAGE_UNSUPPORTED,
    TOO_MANY_REQUESTS,
    // 5xx
    SERVER,
    // 503, the server or its storage is down for now
    SERVICE_UNAVAILABLE,
    // the server was not reached
    NETWORK,
    TIMEOUT,
    // last resort, the only one that shows "unexpected error"
    UNKNOWN,
}

// what the user was doing, the same status means different things in different places
enum class ErrorContext {
    LOGIN,
    REGISTER,
    LOAD_FEED,
    SEARCH,
    LOAD_EVENT,
    SUBSCRIBE,
    UNSUBSCRIBE,
    LOAD_PROFILE,
    UPDATE_PROFILE,
    CHANGE_PASSWORD,
    LOGOUT,
    LOAD_MY_EVENTS,
    CREATE_EVENT,
    UPDATE_EVENT,
    DELETE_EVENT,
    LOAD_EVENT_FORM,
    UPLOAD_IMAGE,
    LOAD_IMAGE,
    GENERIC,
}

fun Throwable.toAppError(context: ErrorContext = ErrorContext.GENERIC): AppError {
    if (this is ApiException) return mapApiError(status, message, isTimeout, context)
    AppLog.e("AppError", "unexpected exception in $context", this)
    return when (context) {
        ErrorContext.UPLOAD_IMAGE -> AppError.IMAGE_UPLOAD_FAILED
        ErrorContext.LOAD_IMAGE -> AppError.IMAGE_LOAD_FAILED
        else -> AppError.UNKNOWN
    }
}

private val EVENT_CONTEXTS = setOf(
    ErrorContext.LOAD_EVENT,
    ErrorContext.SUBSCRIBE,
    ErrorContext.UNSUBSCRIBE,
    ErrorContext.UPDATE_EVENT,
    ErrorContext.DELETE_EVENT,
    ErrorContext.LOAD_EVENT_FORM,
    ErrorContext.UPLOAD_IMAGE,
)

private val FULL_WORD = Regex("""\bfull\b""")

/**
 * Pure mapping of a failed call to an [AppError].
 *
 * status null: the server was not reached, TIMEOUT or NETWORK.
 * 401: INVALID_CREDENTIALS only on LOGIN, everywhere else the session is gone (SESSION_EXPIRED).
 * 403: the server message first ("Only exhibitors", "Only visitors", "Only the organizer", "Current password"),
 *      then the context (CHANGE_PASSWORD, CREATE_EVENT, SUBSCRIBE/UNSUBSCRIBE, event editing and images), else FORBIDDEN.
 * 400/422: EVENT_FINISHED or EVENT_FULL when the message says so, IMAGE_UNSUPPORTED on uploads, else VALIDATION.
 * 404: EVENT_NOT_FOUND in event contexts, IMAGE_LOAD_FAILED for images, else NOT_FOUND.
 * 409: EMAIL_ALREADY_EXISTS on REGISTER, EVENT_FULL on SUBSCRIBE (or a "full" message), else CONFLICT.
 * 5xx: on UPLOAD_IMAGE always IMAGE_UPLOAD_FAILED (503 is usually the image storage), on LOAD_IMAGE IMAGE_LOAD_FAILED,
 *      else 503 is SERVICE_UNAVAILABLE and the rest SERVER.
 */
fun mapApiError(status: Int?, serverMessage: String?, isTimeout: Boolean, context: ErrorContext): AppError {
    val message = serverMessage.orEmpty().lowercase()
    return when (status) {
        null -> if (isTimeout) AppError.TIMEOUT else AppError.NETWORK
        400, 422 -> when {
            "finished" in message -> AppError.EVENT_FINISHED
            FULL_WORD.containsMatchIn(message) -> AppError.EVENT_FULL
            context == ErrorContext.UPLOAD_IMAGE -> AppError.IMAGE_UNSUPPORTED
            else -> AppError.VALIDATION
        }
        401 -> if (context == ErrorContext.LOGIN) AppError.INVALID_CREDENTIALS else AppError.SESSION_EXPIRED
        403 -> when {
            "only exhibitors" in message -> AppError.EXHIBITOR_ONLY
            "only visitors" in message -> AppError.VISITOR_ONLY
            "only the organizer" in message -> AppError.NOT_ORGANIZER
            "current password" in message -> AppError.INCORRECT_PASSWORD
            else -> when (context) {
                ErrorContext.CHANGE_PASSWORD -> AppError.INCORRECT_PASSWORD
                ErrorContext.CREATE_EVENT -> AppError.EXHIBITOR_ONLY
                ErrorContext.SUBSCRIBE, ErrorContext.UNSUBSCRIBE -> AppError.VISITOR_ONLY
                ErrorContext.UPDATE_EVENT, ErrorContext.DELETE_EVENT,
                ErrorContext.LOAD_EVENT_FORM, ErrorContext.UPLOAD_IMAGE -> AppError.NOT_ORGANIZER
                else -> AppError.FORBIDDEN
            }
        }
        404 -> when (context) {
            in EVENT_CONTEXTS -> AppError.EVENT_NOT_FOUND
            ErrorContext.LOAD_IMAGE -> AppError.IMAGE_LOAD_FAILED
            else -> AppError.NOT_FOUND
        }
        408 -> AppError.TIMEOUT
        409 -> when {
            context == ErrorContext.REGISTER -> AppError.EMAIL_ALREADY_EXISTS
            context == ErrorContext.SUBSCRIBE || FULL_WORD.containsMatchIn(message) -> AppError.EVENT_FULL
            "email" in message -> AppError.EMAIL_ALREADY_EXISTS
            else -> AppError.CONFLICT
        }
        413 -> AppError.FILE_TOO_LARGE
        415 -> if (context == ErrorContext.UPLOAD_IMAGE) AppError.IMAGE_UNSUPPORTED else AppError.VALIDATION
        429 -> AppError.TOO_MANY_REQUESTS
        in 500..599 -> when {
            context == ErrorContext.UPLOAD_IMAGE -> AppError.IMAGE_UPLOAD_FAILED
            context == ErrorContext.LOAD_IMAGE -> AppError.IMAGE_LOAD_FAILED
            status == 503 -> AppError.SERVICE_UNAVAILABLE
            else -> AppError.SERVER
        }
        else -> when (context) {
            ErrorContext.UPLOAD_IMAGE -> AppError.IMAGE_UPLOAD_FAILED
            ErrorContext.LOAD_IMAGE -> AppError.IMAGE_LOAD_FAILED
            else -> AppError.UNKNOWN
        }
    }
}

// warnings for things the user can fix or wait out, errors for the rest
fun AppError.defaultSeverity(): Severity = when (this) {
    AppError.VALIDATION, AppError.EVENT_FULL, AppError.EVENT_FINISHED, AppError.FILE_TOO_LARGE,
    AppError.IMAGE_UNSUPPORTED, AppError.TOO_MANY_REQUESTS -> Severity.WARN
    else -> Severity.ERROR
}
