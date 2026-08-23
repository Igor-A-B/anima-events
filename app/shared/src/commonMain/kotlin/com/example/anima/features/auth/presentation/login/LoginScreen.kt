package com.example.anima.features.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_button_enter
import anima.app.shared.generated.resources.login_password_hint
import com.example.anima.core.components.brand.AnimaBrand
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideEye
import com.example.anima.core.components.icon.lucide.LucideEyeOff
import com.example.anima.core.components.icon.lucide.LucideLock
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.login.components.BiometricButton
import com.example.anima.features.auth.presentation.login.components.UserAccountCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen() {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(AnimaTheme.colors.background)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimaBrand()

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxl))

        UserAccountCard(
            name = "John Doe",
            email = "jo*******@gmail.com",
            onSwitch = { },
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.huge))

        AnimaTextField(
            value = password,
            onValueChange = { password = it },
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

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.huge))

        AnimaButton(
            text = stringResource(Res.string.core_button_enter),
            onClick = { },
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.huge))

        BiometricButton(
            onClick = { },
        )
    }
}