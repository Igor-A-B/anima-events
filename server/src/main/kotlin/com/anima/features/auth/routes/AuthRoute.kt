package com.anima.features.auth.routes

import com.anima.features.auth.services.AuthService
import com.anima.features.auth.dtos.LoginRequestDto
import com.anima.features.auth.dtos.RefreshRequestDto
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.auth.exceptions.EmailAlreadyExistsException
import com.anima.features.auth.dtos.TokenResponseDto
import com.anima.features.auth.exceptions.InvalidCredentialsException
import com.anima.features.auth.exceptions.InvalidRefreshTokenException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthRoute(private val authService: AuthService) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDto): ResponseEntity<TokenResponseDto> {
        val tokens = authService.login(request.email, request.password)
        return ResponseEntity.ok(TokenResponseDto(tokens.accessToken, tokens.refreshToken))
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequestDto): ResponseEntity<TokenResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request))

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequestDto): ResponseEntity<TokenResponseDto> {
        val tokens = authService.refresh(request.refreshToken)
        return ResponseEntity.ok(TokenResponseDto(tokens.accessToken, tokens.refreshToken))
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshRequestDto): ResponseEntity<Void> {
        authService.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(InvalidCredentialsException::class, InvalidRefreshTokenException::class)
    fun handleAuthErrors(ex: RuntimeException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to (ex.message ?: HttpStatus.UNAUTHORIZED.name)))

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailTaken(ex: RuntimeException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(mapOf("error" to (ex.message ?: HttpStatus.CONFLICT.name)))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalid(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: HttpStatus.BAD_REQUEST.name)))
}
