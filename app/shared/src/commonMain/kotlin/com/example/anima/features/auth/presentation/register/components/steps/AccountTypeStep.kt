package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_step1_exhibit_Card_subtitle
import anima.app.shared.generated.resources.register_step1_exhibit_Card_title
import anima.app.shared.generated.resources.register_step1_participate_card_subtitle
import anima.app.shared.generated.resources.register_step1_participate_card_title
import anima.app.shared.generated.resources.register_step1_subtitle
import anima.app.shared.generated.resources.register_step1_title
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideStore
import com.example.anima.core.components.icon.lucide.LucideUsers
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.register.AccountType
import org.jetbrains.compose.resources.stringResource

@Composable
fun AccountTypeStep(
    selected: AccountType?,
    onSelect: (AccountType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.register_step1_title),
            style = AnimaTheme.typography.headlineMedium,
            color = AnimaTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.sm))

        Text(
            text = stringResource(Res.string.register_step1_subtitle),
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxxl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md)
        ) {
            AccountTypeCard(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.register_step1_participate_card_title),
                subtitle = stringResource(Res.string.register_step1_participate_card_subtitle),
                icon = {
                    AnimaIcon(
                        imageVector = LucideUsers,
                        contentDescription = null,
                        tint = AnimaTheme.colors.primary
                    )
                },
                selected = selected == AccountType.VISITOR,
                onClick = { onSelect(AccountType.VISITOR) },
            )

            AccountTypeCard(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.register_step1_exhibit_Card_title),
                subtitle = stringResource(Res.string.register_step1_exhibit_Card_subtitle),
                icon = {
                    AnimaIcon(
                        imageVector = LucideStore,
                        contentDescription = null,
                        tint = AnimaTheme.colors.primary
                    )
                },
                selected = selected == AccountType.EXHIBITOR,
                onClick = { onSelect(AccountType.EXHIBITOR) },
            )
        }
    }
}

@Composable
private fun AccountTypeCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) {
        AnimaTheme.colors.primary
    } else {
        AnimaTheme.colors.surface
    }


    Column(
        modifier = modifier
            .clip(AnimaTheme.shapes.large)
            .background(AnimaTheme.colors.surface)
            .border(1.5.dp, borderColor, AnimaTheme.shapes.large)
            .clickable { onClick() }
            .padding(AnimaTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)
    ) {
        icon()

        Text(
            text = title,
            style = AnimaTheme.typography.labelLarge,
            color = AnimaTheme.colors.onSurface,
        )

        Text(
            text = subtitle,
            style = AnimaTheme.typography.bodySmall,
            color = AnimaTheme.colors.onSurfaceVariant,
        )
    }
}