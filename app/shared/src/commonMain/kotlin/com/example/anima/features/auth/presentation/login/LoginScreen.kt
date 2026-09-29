package com.example.anima.features.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_button_enter
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.login_email_hint
import anima.app.shared.generated.resources.login_error_invalid_credentials
import anima.app.shared.generated.resources.login_no_account
import anima.app.shared.generated.resources.login_password_hint
import anima.app.shared.generated.resources.login_register
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.brand.AnimaBrand
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideEye
import com.example.anima.core.components.icon.lucide.LucideEyeOff
import com.example.anima.core.components.icon.lucide.LucideLock
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.login.components.BiometricButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    AnimaScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AnimaTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = AnimaTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimaBrand()

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxl))

            AnimaTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                placeholder = stringResource(Res.string.login_email_hint),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.lg))

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.huge))

            AnimaTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = stringResource(Res.string.login_password_hint),
                leadingIcon = {
                    AnimaIcon(
                        imageVector = LucideLock,
                        contentDescription = null,
                        tint = AnimaTheme.colors.onSurfaceVariant,
                        size = 20.dp,
                    )
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                    ) {
                        AnimaIcon(
                            imageVector = if (passwordVisible) LucideEyeOff else LucideEye,
                            tint = AnimaTheme.colors.onSurfaceVariant,
                            size = 20.dp,
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            )

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(AnimaTheme.spacing.md))
                Text(
                    text = stringResource(
                        if (error == LoginError.INVALID_CREDENTIALS) Res.string.login_error_invalid_credentials
                        else Res.string.core_error_generic,
                    ),
                    style = AnimaTheme.typography.bodySmall,
                    color = AnimaTheme.colors.error,
                )
            }

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.huge))

            AnimaButton(
                text = stringResource(Res.string.core_button_enter),
                onClick = { viewModel.onSubmit(onLoginSuccess) },
                enabled = uiState.canSubmit,
            )

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.md))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.login_no_account),
                    style = AnimaTheme.typography.bodySmall,
                    color = AnimaTheme.colors.onSurfaceVariant,
                )
                Text(
                    text = stringResource(Res.string.login_register),
                    style = AnimaTheme.typography.bodySmall,
                    color = AnimaTheme.colors.primary,
                    modifier = Modifier.clickable { onNavigateToRegister() },
                )
            }

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.huge))

            BiometricButton(
                onClick = { },
            )
        }
    }
}