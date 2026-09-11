package com.example.anima.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.profile_unverified_body
import anima.app.shared.generated.resources.profile_unverified_title
import anima.app.shared.generated.resources.profile_verify_action
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.button.AnimaButtonSize
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideShieldAlert
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

// only shown to an exhibitor whose account is not verified yet
@Composable
fun VerificationCard(
    onValidate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AnimaTheme.shapes.large)
            .background(AnimaTheme.colors.surfaceVariant)
            .border(
                width = 1.dp,
                color = AnimaTheme.colors.primary,
                shape = AnimaTheme.shapes.large,
            )
            .padding(AnimaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            AnimaIcon(
                imageVector = LucideShieldAlert,
                contentDescription = null,
                tint = AnimaTheme.colors.error,
                size = 20.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.profile_unverified_title),
                    style = AnimaTheme.typography.titleSmall,
                    color = AnimaTheme.colors.onSurface,
                )

                Text(
                    text = stringResource(Res.string.profile_unverified_body),
                    style = AnimaTheme.typography.bodySmall,
                    color = AnimaTheme.colors.onSurfaceVariant,
                )
            }
        }

        AnimaButton(
            text = stringResource(Res.string.profile_verify_action),
            onClick = onValidate,
            size = AnimaButtonSize.COMPACT,
        )
    }
}
