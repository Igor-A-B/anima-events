package com.example.anima.features.auth.presentation.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_button_enter
import anima.app.shared.generated.resources.core_button_next
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.error.messageRes
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.register.components.RegisterHeader
import com.example.anima.features.auth.presentation.register.components.steps.AccountTypeStep
import com.example.anima.features.auth.presentation.register.components.steps.EmailStep
import com.example.anima.features.auth.presentation.register.components.steps.NameStep
import com.example.anima.features.auth.presentation.register.components.steps.PasswordStep
import com.example.anima.features.auth.presentation.register.components.steps.SuccessStep
import com.example.anima.navigation.horizontalSlideTransition
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterComplete: () -> Unit = {},
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimaScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AnimaTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = AnimaTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (uiState.step < uiState.totalSteps) {
                RegisterHeader(
                    currentStep = uiState.step,
                    totalSteps = uiState.totalSteps - 1,
                    onBack = if (uiState.step > 1) viewModel::onPreviousStep else onNavigateBack,
                )

                Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))
            }

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))

            AnimatedContent(
                targetState = uiState.step,
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    horizontalSlideTransition(forward = targetState > initialState)
                },
                label = "RegisterStepContent",
            ) { step ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                ) {
                    when (step) {
                        1 -> AccountTypeStep(
                            selected = uiState.accountType,
                            onSelect = viewModel::onAccountTypeSelected,
                        )

                        2 -> NameStep(
                            accountType = uiState.accountType,
                            value = uiState.name,
                            onValueChange = viewModel::onNameChanged,
                        )

                        3 -> EmailStep(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                        )

                        4 -> PasswordStep(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                        )

                        5 -> SuccessStep()
                    }
                }
            }

            // TODO(snackbar): replace with a snackbar fed by viewModel.events
            uiState.error?.let { error ->
                Text(
                    text = stringResource(error.messageRes()),
                    style = AnimaTheme.typography.bodySmall,
                    color = AnimaTheme.colors.error,
                )
                Spacer(modifier = Modifier.height(AnimaTheme.spacing.sm))
            }

            AnimaButton(
                text = stringResource(
                    when {
                        uiState.isLastStep -> Res.string.core_button_enter
                        else -> Res.string.core_button_next
                    }
                ),
                onClick = {
                    when {
                        uiState.isLastStep -> onRegisterComplete()
                        uiState.step == uiState.totalSteps - 1 -> viewModel.onSubmit(onSuccess = viewModel::onNextStep)
                        else -> viewModel.onNextStep()
                    }
                },
                enabled = uiState.canAdvance,
                loading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(AnimaTheme.spacing.sm))
        }
    }
}