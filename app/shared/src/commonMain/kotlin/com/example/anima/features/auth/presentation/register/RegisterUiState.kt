package com.example.anima.features.auth.presentation.register

import com.anima.features.user.models.AccountType
import com.example.anima.core.error.AppError
import com.example.anima.features.auth.presentation.register.components.steps.isPasswordStrong

data class RegisterUiState(
    val step: Int = 1,
    val accountType: AccountType? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null
) {
    val totalSteps: Int = 5

    val isLastStep: Boolean = step == totalSteps

    val canAdvance: Boolean = when (step) {
        1 -> accountType != null
        2 -> name.isNotBlank()
        3 -> email.isNotBlank()
        4 -> isPasswordStrong(password)
        5 -> true  // success step
        else -> false
    }
}