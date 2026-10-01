package com.example.anima.features.auth.presentation.login

import com.example.anima.core.error.AppError

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null,
) {
    val canSubmit: Boolean = email.isNotBlank() && password.isNotBlank() && !isLoading
}
