package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_space_name_hint
import anima.app.shared.generated.resources.register_step2_space_subtitle
import anima.app.shared.generated.resources.register_step2_space_title
import anima.app.shared.generated.resources.register_step2_user_subtitle
import anima.app.shared.generated.resources.register_step2_user_title
import anima.app.shared.generated.resources.register_user_name_hint
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideStore
import com.example.anima.core.components.icon.lucide.LucideUser
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.register.AccountType
import org.jetbrains.compose.resources.stringResource

@Composable
fun NameStep(
    accountType: AccountType?,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(
        if (accountType == AccountType.EXHIBITOR) {
            Res.string.register_step2_space_title
        } else {
            Res.string.register_step2_user_title
        }
    )

    val subtitle = stringResource(
        if (accountType == AccountType.EXHIBITOR) {
            Res.string.register_step2_space_subtitle
        } else {
            Res.string.register_step2_user_subtitle

        }
    )

    val hint = stringResource(
        if (accountType == AccountType.EXHIBITOR) {
            Res.string.register_space_name_hint
        } else {
            Res.string.register_user_name_hint
        }
    )


    Column(modifier = modifier) {
        Text(
            text = title,
            style = AnimaTheme.typography.headlineMedium,
            color = AnimaTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.sm))

        Text(
            text = subtitle,
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))

        AnimaTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = hint,
            leadingIcon = {
                AnimaIcon(
                    imageVector = if (accountType == AccountType.EXHIBITOR) LucideStore else LucideUser,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = 20.dp,
                )
            },
        )
    }
}