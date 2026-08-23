package com.example.anima.features.auth.core.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_button_change
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.button.AnimaButtonSize
import com.example.anima.core.components.button.AnimaButtonVariant
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCircleUser
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserAccountCard(
    name: String,
    email: String,
    onSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AnimaTheme.shapes.large)
            .background(AnimaTheme.colors.surface)
            .padding(AnimaTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {

        AnimaIcon(
            imageVector = LucideCircleUser,
            contentDescription = null,
            tint = AnimaTheme.colors.onSurfaceVariant,
            size = 32.dp,
        )


        // name and email
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
        ) {
            Text(
                text = name,
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = email,
                style = AnimaTheme.typography.bodySmall,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // change account button
        AnimaButton(
            text = stringResource(Res.string.core_button_change),
            onClick = onSwitch,
            variant = AnimaButtonVariant.OUTLINED,
            size = AnimaButtonSize.COMPACT,
        )
    }
}