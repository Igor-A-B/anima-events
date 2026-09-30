package com.anima.features.user.routes

import com.anima.features.user.dtos.ChangePasswordRequestDto
import com.anima.features.user.dtos.MeResponseDto
import com.anima.features.user.exceptions.IncorrectPasswordException
import com.anima.features.user.models.AccountType
import com.anima.features.user.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/me")
class UserRoute(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(UserRoute::class.java)

    @GetMapping
    fun me(auth: Authentication): ResponseEntity<MeResponseDto> =
        ResponseEntity.ok(userService.me(UUID.fromString(auth.name), accountTypeOf(auth)))

    @PostMapping("/password")
    fun changePassword(auth: Authentication, @RequestBody request: ChangePasswordRequestDto): ResponseEntity<Void> {
        userService.changePassword(UUID.fromString(auth.name), request.currentPassword, request.newPassword)
        return ResponseEntity.noContent().build()
    }

    // the JwtAuthFilter turns the accountType claim into a ROLE_ authority
    private fun accountTypeOf(auth: Authentication): AccountType =
        AccountType.valueOf(auth.authorities.firstNotNullOf { it.authority }.removePrefix("ROLE_"))

    // 403, not 401: a 401 makes the app try a token refresh
    @ExceptionHandler(IncorrectPasswordException::class)
    fun handleIncorrectPassword(ex: IncorrectPasswordException): ResponseEntity<Map<String, String>> =
        error(HttpStatus.FORBIDDEN, ex.message)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalid(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        error(HttpStatus.BAD_REQUEST, ex.message)

    // whatever else goes wrong was already rolled back by the service
    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<Map<String, String>> {
        log.error("Unexpected error on /me", ex)
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, nothing was changed")
    }

    private fun error(status: HttpStatus, message: String?) =
        ResponseEntity.status(status).body(mapOf("error" to (message ?: status.name)))
}
