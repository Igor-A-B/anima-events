package com.example.anima.features.auth.presentation.register

enum class AccountType { VISITOR, EXHIBITOR }

data class RegisterUiState(
    val step: Int = 1,
    val accountType: AccountType? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String = ""
) {
    val totalSteps: Int = 4

    val isLastStep: Boolean = step == totalSteps

    val canAdvance: Boolean = when (step) {
        1 -> accountType != null
        2 -> name.isNotBlank()
        3 -> email.isNotBlank()
        4 -> password.isNotBlank()
        else -> false
    }
}