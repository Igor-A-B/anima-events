package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCircle
import com.example.anima.core.components.icon.lucide.LucideCircleCheck
import com.example.anima.core.theme.AnimaTheme

@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier,
) {
    val requirements = evaluatePassword(password)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        requirements.forEach { requirement ->
            PasswordRequirementRow(requirement = requirement)
        }
    }
}

@Composable
private fun PasswordRequirementRow(
    requirement: PasswordRequirement,
) {
    val color by animateColorAsState(
        targetValue = if (requirement.isMet)
            AnimaTheme.colors.success
        else
            AnimaTheme.colors.onSurfaceVariant,
        animationSpec = tween(300),
        label = "RequirementColor",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),

        ) {
        AnimaIcon(
            imageVector = if (requirement.isMet) LucideCircleCheck else LucideCircle,
            contentDescription = null,
            tint = color,
            size = 16.dp,
        )

        Text(
            text = requirement.label,
            style = AnimaTheme.typography.bodyMedium,
            color = color,
        )
    }
}