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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_button_enter
import anima.app.shared.generated.resources.login_email_hint
import anima.app.shared.generated.resources.login_no_account
import anima.app.shared.generated.resources.login_password_hint
import anima.app.shared.generated.resources.login_register
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.brand.AnimaBrand
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideEye
import com.example.anima.core.components.icon.lucide.LucideEyeOff
import com.example.anima.core.components.icon.lucide.LucideLock
import com.example.anima.core.components.icon.lucide.LucideMail
import com.example.anima.core.components.textfield.AnimaTextField
import com.example.anima.core.error.messageRes
import com.example.anima.core.theme.AnimaTheme
// import com.example.anima.features.auth.presentation.login.components.BiometricButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))

            // fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxxl),
            ) {
                AnimaTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    placeholder = stringResource(Res.string.login_email_hint),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        AnimaIcon(
                            imageVector = LucideMail,
                            contentDescription = null,
                            tint = AnimaTheme.colors.onSurfaceVariant,
                            size = 20.dp,
                        )
                    },
                )

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
                        Box(modifier = Modifier.clickable { passwordVisible = !passwordVisible }) {
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
                    Text(
                        text = stringResource(error.messageRes()),
                        style = AnimaTheme.typography.bodySmall,
                        color = AnimaTheme.colors.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))

            AnimaButton(
                text = stringResource(Res.string.core_button_enter),
                onClick = { viewModel.onSubmit(onLoginSuccess) },
                enabled = uiState.canSubmit,
                loading = uiState.isLoading,
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

            // TODO: biometrics
            // Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))
            //
            // BiometricButton(onClick = { })
        }
    }
}