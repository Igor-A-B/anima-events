package com.example.anima.core.error

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.jetbrains.compose.resources.StringResource

// the one place an AppException goes to be shown, App() listens to it
class AppExceptionBus {
    private val _exceptions = MutableSharedFlow<AppException>(
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val exceptions: SharedFlow<AppException> = _exceptions.asSharedFlow()

    fun report(exception: Throwable, context: ErrorContext = ErrorContext.GENERIC) {
        _exceptions.tryEmit(exception.toAppException(context))
    }

    // for an error already mapped, like one also shown inline
    fun report(error: AppError) {
        _exceptions.tryEmit(error.toAppException())
    }

    // for non errors too, like a success or a notification
    fun show(messageRes: StringResource, severity: Severity) {
        _exceptions.tryEmit(AppException(messageRes, severity))
    }

    // install in a scope to report whatever it throws
    val exceptionHandler = CoroutineExceptionHandler { _, throwable -> report(throwable) }
}
