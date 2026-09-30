package com.example.anima.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.profile_cancel
import anima.app.shared.generated.resources.profile_password_current_hint
import anima.app.shared.generated.resources.profile_password_new_hint
import anima.app.shared.generated.resources.profile_password_save
import anima.app.shared.generated.resources.profile_password_title
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.error.AppError
import com.example.anima.core.error.messageRes
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

private const val MIN_PASSWORD_LENGTH = 8

@Composable
fun ChangePasswordDialog(
    isSaving: Boolean,
    error: AppError?,
    onSave: (currentPassword: String, newPassword: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var current by rememberSaveable { mutableStateOf("") }
    var new by rememberSaveable { mutableStateOf("") }
    val canSave = current.isNotBlank() && new.length >= MIN_PASSWORD_LENGTH && !isSaving

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AnimaTheme.colors.surface,
        title = {
            Text(
                text = stringResource(Res.string.profile_password_title),
                style = AnimaTheme.typography.titleLarge,
                color = AnimaTheme.colors.onSurface,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md)) {
                AnimaTextField(
                    value = current,
                    onValueChange = { current = it },
                    placeholder = stringResource(Res.string.profile_password_current_hint),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !isSaving,
                    isError = error == AppError.INCORRECT_PASSWORD,
                )
                AnimaTextField(
                    value = new,
                    onValueChange = { new = it },
                    placeholder = stringResource(Res.string.profile_password_new_hint),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !isSaving,
                    isError = error == AppError.VALIDATION,
                )
                if (error != null) {
                    Text(
                        text = stringResource(error.messageRes()),
                        style = AnimaTheme.typography.labelSmall,
                        color = AnimaTheme.colors.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(current, new) }, enabled = canSave) {
                Text(text = stringResource(Res.string.profile_password_save), color = AnimaTheme.colors.primaryVariant)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(text = stringResource(Res.string.profile_cancel), color = AnimaTheme.colors.onSurfaceVariant)
            }
        },
    )
}
