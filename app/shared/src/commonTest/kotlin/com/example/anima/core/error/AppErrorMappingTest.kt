package com.example.anima.core.error

import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_generic
import com.example.anima.core.error.ErrorContext.CHANGE_PASSWORD
import com.example.anima.core.error.ErrorContext.CREATE_EVENT
import com.example.anima.core.error.ErrorContext.DELETE_EVENT
import com.example.anima.core.error.ErrorContext.GENERIC
import com.example.anima.core.error.ErrorContext.LOAD_EVENT
import com.example.anima.core.error.ErrorContext.LOAD_EVENT_FORM
import com.example.anima.core.error.ErrorContext.LOAD_FEED
import com.example.anima.core.error.ErrorContext.LOAD_PROFILE
import com.example.anima.core.error.ErrorContext.LOGIN
import com.example.anima.core.error.ErrorContext.REGISTER
import com.example.anima.core.error.ErrorContext.SEARCH
import com.example.anima.core.error.ErrorContext.SUBSCRIBE
import com.example.anima.core.error.ErrorContext.UNSUBSCRIBE
import com.example.anima.core.error.ErrorContext.UPDATE_EVENT
import com.example.anima.core.error.ErrorContext.UPLOAD_IMAGE
import com.example.anima.core.network.ApiException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AppErrorMappingTest {

    private fun map(status: Int?, context: ErrorContext = GENERIC, message: String = "", timeout: Boolean = false) =
        ApiException(status, message, isTimeout = timeout).toAppError(context)

    @Test
    fun noStatusIsNetworkOrTimeout() {
        assertEquals(AppError.NETWORK, map(null))
        assertEquals(AppError.TIMEOUT, map(null, timeout = true))
        assertEquals(AppError.TIMEOUT, map(null, CREATE_EVENT, timeout = true))
        assertEquals(AppError.TIMEOUT, map(408))
    }

    @Test
    fun badRequestIsValidationUnlessTheMessageSaysMore() {
        assertEquals(AppError.VALIDATION, map(400))
        assertEquals(AppError.VALIDATION, map(400, CREATE_EVENT, "Bad Request"))
        assertEquals(AppError.VALIDATION, map(422, REGISTER))
        assertEquals(AppError.EVENT_FINISHED, map(400, SUBSCRIBE, "This event is already finished"))
        assertEquals(AppError.EVENT_FULL, map(400, SUBSCRIBE, "Event is full"))
        assertEquals(AppError.IMAGE_UNSUPPORTED, map(400, UPLOAD_IMAGE))
        assertEquals(AppError.IMAGE_UNSUPPORTED, map(415, UPLOAD_IMAGE))
    }

    @Test
    fun unauthorizedIsWrongCredentialsOnlyOnLogin() {
        assertEquals(AppError.INVALID_CREDENTIALS, map(401, LOGIN, "Invalid email or password"))
        for (context in ErrorContext.entries - LOGIN) {
            assertEquals(AppError.SESSION_EXPIRED, map(401, context), "401 in $context")
        }
    }

    @Test
    fun forbiddenDependsOnTheMessageAndTheContext() {
        assertEquals(AppError.EXHIBITOR_ONLY, map(403, CREATE_EVENT, "Only exhibitors can create events"))
        assertEquals(AppError.EXHIBITOR_ONLY, map(403, CREATE_EVENT))
        assertEquals(AppError.EXHIBITOR_ONLY, map(403, GENERIC, "Only exhibitors can create events"))
        assertEquals(AppError.VISITOR_ONLY, map(403, SUBSCRIBE, "Only visitors can subscribe to events"))
        assertEquals(AppError.VISITOR_ONLY, map(403, SUBSCRIBE))
        assertEquals(AppError.VISITOR_ONLY, map(403, UNSUBSCRIBE))
        assertEquals(AppError.NOT_ORGANIZER, map(403, UPDATE_EVENT, "Only the organizer can change this event"))
        assertEquals(AppError.NOT_ORGANIZER, map(403, UPDATE_EVENT))
        assertEquals(AppError.NOT_ORGANIZER, map(403, DELETE_EVENT))
        assertEquals(AppError.NOT_ORGANIZER, map(403, LOAD_EVENT_FORM))
        assertEquals(AppError.NOT_ORGANIZER, map(403, UPLOAD_IMAGE))
        assertEquals(AppError.INCORRECT_PASSWORD, map(403, CHANGE_PASSWORD, "Current password is incorrect"))
        assertEquals(AppError.INCORRECT_PASSWORD, map(403, CHANGE_PASSWORD))
        assertEquals(AppError.FORBIDDEN, map(403, LOAD_PROFILE))
        assertEquals(AppError.FORBIDDEN, map(403))
    }

    @Test
    fun notFoundIsEventSpecificInEventContexts() {
        assertEquals(AppError.EVENT_NOT_FOUND, map(404, LOAD_EVENT, "Event not found"))
        assertEquals(AppError.EVENT_NOT_FOUND, map(404, UPDATE_EVENT))
        assertEquals(AppError.EVENT_NOT_FOUND, map(404, SUBSCRIBE))
        assertEquals(AppError.NOT_FOUND, map(404, LOAD_PROFILE))
        assertEquals(AppError.NOT_FOUND, map(404))
    }

    @Test
    fun conflictDependsOnTheContext() {
        assertEquals(AppError.EMAIL_ALREADY_EXISTS, map(409, REGISTER, "Email already registered"))
        assertEquals(AppError.EVENT_FULL, map(409, SUBSCRIBE))
        assertEquals(AppError.EVENT_FULL, map(409, GENERIC, "The event is full"))
        assertEquals(AppError.CONFLICT, map(409, UPDATE_EVENT))
        assertEquals(AppError.CONFLICT, map(409))
    }

    @Test
    fun otherClientErrors() {
        assertEquals(AppError.FILE_TOO_LARGE, map(413, UPLOAD_IMAGE, "file is too large (max 10MB)"))
        assertEquals(AppError.FILE_TOO_LARGE, map(413))
        assertEquals(AppError.TOO_MANY_REQUESTS, map(429, LOGIN, "Too many requests, try again later"))
        assertEquals(AppError.TOO_MANY_REQUESTS, map(429, SEARCH))
        assertEquals(AppError.UNKNOWN, map(418))
        assertEquals(AppError.IMAGE_UPLOAD_FAILED, map(418, UPLOAD_IMAGE))
    }

    @Test
    fun serverErrors() {
        assertEquals(AppError.SERVER, map(500, CREATE_EVENT, "Internal Server Error"))
        assertEquals(AppError.SERVER, map(502, LOAD_FEED))
        assertEquals(AppError.SERVICE_UNAVAILABLE, map(503, LOAD_FEED))
        assertEquals(AppError.IMAGE_UPLOAD_FAILED, map(503, UPLOAD_IMAGE, "storage is unavailable"))
        assertEquals(AppError.IMAGE_UPLOAD_FAILED, map(500, UPLOAD_IMAGE))
    }

    @Test
    fun anythingElseIsUnknownExceptForImages() {
        val boom = IllegalStateException("boom")
        assertEquals(AppError.UNKNOWN, boom.toAppError())
        assertEquals(AppError.UNKNOWN, boom.toAppError(CREATE_EVENT))
        assertEquals(AppError.IMAGE_UPLOAD_FAILED, boom.toAppError(UPLOAD_IMAGE))
    }

    @Test
    fun theMappingIsPure() {
        assertEquals(AppError.SESSION_EXPIRED, mapApiError(401, "Invalid email or password", false, CREATE_EVENT))
        assertEquals(AppError.EXHIBITOR_ONLY, mapApiError(403, "Only exhibitors can create events", false, CREATE_EVENT))
    }

    @Test
    fun everyErrorHasAMessageAndOnlyUnknownIsGeneric() {
        for (error in AppError.entries) {
            val res = error.messageRes()
            if (error == AppError.UNKNOWN) {
                assertEquals(Res.string.core_error_generic, res)
            } else {
                assertNotEquals(Res.string.core_error_generic, res, "$error shows the generic message")
            }
        }
    }

    @Test
    fun reportingKeepsTheContext() {
        val exception = ApiException(401, "").toAppException(CREATE_EVENT)
        assertEquals(AppError.SESSION_EXPIRED.messageRes(), exception.messageRes)
        assertEquals(Severity.ERROR, exception.severity)
        assertEquals(Severity.WARN, ApiException(400, "").toAppException(CREATE_EVENT).severity)
    }
}
