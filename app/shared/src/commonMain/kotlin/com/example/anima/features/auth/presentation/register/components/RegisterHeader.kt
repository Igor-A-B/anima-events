package com.example.anima.features.auth.presentation.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_stepper
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideArrowLeft
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterHeader(
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AnimaIcon(
            imageVector = LucideArrowLeft,
            contentDescription = null,
            tint = AnimaTheme.colors.primary,
            modifier = Modifier.clickable { onBack() },
        )

        Text(
            text = buildAnnotatedString {
                append("${stringResource(Res.string.register_stepper)} $currentStep / ")
                withStyle(SpanStyle(color = AnimaTheme.colors.primary)) {
                    append("$totalSteps")
                }
            },
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onBackground,
        )
    }
}