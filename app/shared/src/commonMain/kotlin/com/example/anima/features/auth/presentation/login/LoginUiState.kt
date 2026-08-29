package com.example.anima.features.auth.presentation.login


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val canSubmit: Boolean = password.isNotBlank()
}