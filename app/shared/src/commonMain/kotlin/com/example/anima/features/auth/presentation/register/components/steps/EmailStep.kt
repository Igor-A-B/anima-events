package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_email_hint
import anima.app.shared.generated.resources.register_step3_subtitle
import anima.app.shared.generated.resources.register_step3_title
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideMail
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmailStep(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.register_step3_title),
            style = AnimaTheme.typography.headlineMedium,
            color = AnimaTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.sm))

        Text(
            text = stringResource(Res.string.register_step3_subtitle),
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))

        AnimaTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(Res.string.register_email_hint),
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
    }
}