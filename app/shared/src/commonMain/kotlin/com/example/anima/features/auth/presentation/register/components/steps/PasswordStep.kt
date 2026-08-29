package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_password_hint
import anima.app.shared.generated.resources.register_step4_subtitle
import anima.app.shared.generated.resources.register_step4_title
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideEye
import com.example.anima.core.components.icon.lucide.LucideEyeOff
import com.example.anima.core.components.icon.lucide.LucideLock
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordStep(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)) {
            Text(
                text = stringResource(Res.string.register_step4_title),
                style = AnimaTheme.typography.headlineMedium,
                color = AnimaTheme.colors.onBackground,
            )
            Text(
                text = stringResource(Res.string.register_step4_subtitle),
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
            )
        }

        AnimaTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(Res.string.register_password_hint),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
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
                        contentDescription = null,
                        tint = AnimaTheme.colors.onSurfaceVariant,
                        size = 20.dp,
                    )
                }
            },
        )

        PasswordStrengthIndicator(password = value)
    }
}