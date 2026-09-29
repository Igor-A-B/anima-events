package com.example.anima.features.auth.presentation.login

enum class LoginError { INVALID_CREDENTIALS, GENERIC }

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: LoginError? = null,
) {
    val canSubmit: Boolean = email.isNotBlank() && password.isNotBlank() && !isLoading
}
