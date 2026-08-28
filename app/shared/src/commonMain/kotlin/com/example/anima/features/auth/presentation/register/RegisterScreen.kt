package com.example.anima.features.auth.presentation.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_button_confirm
import anima.app.shared.generated.resources.core_button_next
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.register.components.RegisterHeader
import com.example.anima.features.auth.presentation.register.components.steps.AccountTypeStep
import com.example.anima.features.auth.presentation.register.components.steps.EmailStep
import com.example.anima.features.auth.presentation.register.components.steps.NameStep
import com.example.anima.features.auth.presentation.register.components.steps.PasswordStep
import com.example.anima.navigation.horizontalSlideTransition
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterComplete: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .background(AnimaTheme.colors.background)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RegisterHeader(
            currentStep = uiState.step,
            totalSteps = uiState.totalSteps,
            onBack = if (uiState.step > 1) viewModel::onPreviousStep else onNavigateBack,
        )

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
                }
            }
        }

        AnimaButton(
            text = stringResource(
                if (uiState.isLastStep) Res.string.core_button_confirm
                else Res.string.core_button_next
            ),
            onClick = {
                if (uiState.isLastStep) {
                    viewModel.onSubmit()
                    onRegisterComplete()
                } else {
                    viewModel.onNextStep()
                }
            },
            enabled = uiState.canAdvance,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.xl))
    }
}