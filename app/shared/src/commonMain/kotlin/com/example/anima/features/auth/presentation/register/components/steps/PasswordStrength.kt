package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.runtime.Composable
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_password_strength_length
import anima.app.shared.generated.resources.register_password_strength_lowercase
import anima.app.shared.generated.resources.register_password_strength_number
import anima.app.shared.generated.resources.register_password_strength_special
import anima.app.shared.generated.resources.register_password_strength_uppercase
import org.jetbrains.compose.resources.stringResource

data class PasswordRequirement(
    val label: String,
    val isMet: Boolean,
)

@Composable
fun evaluatePassword(password: String): List<PasswordRequirement> = listOf(
    PasswordRequirement(
        label = stringResource(Res.string.register_password_strength_length),
        isMet = password.length >= 8,
    ),
    PasswordRequirement(
        label = stringResource(Res.string.register_password_strength_uppercase),
        isMet = password.any { it.isUpperCase() },
    ),
    PasswordRequirement(
        label = stringResource(Res.string.register_password_strength_lowercase),
        isMet = password.any { it.isLowerCase() },
    ),
    PasswordRequirement(
        label = stringResource(Res.string.register_password_strength_number),
        isMet = password.any { it.isDigit() },
    ),
    PasswordRequirement(
        label = stringResource(Res.string.register_password_strength_special),
        isMet = password.any { !it.isLetterOrDigit() },
    ),
)

fun isPasswordStrong(password: String): Boolean =
    password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() }