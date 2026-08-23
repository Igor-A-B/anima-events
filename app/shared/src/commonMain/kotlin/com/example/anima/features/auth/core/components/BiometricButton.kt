package com.example.anima.features.auth.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.login_enter_with_biometrics
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideFingerprint
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun BiometricButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = AnimaTheme.colors.primary

    val label = stringResource(Res.string.login_enter_with_biometrics)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(BiometricButtonDefaults.Size)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primary.copy(alpha = 1f),
                                primary.copy(alpha = 0f),
                            ),
                            radius = size.minDimension * 0.7f,
                        ),
                        radius = size.minDimension * 0.7f,
                    )
                }
                .border(
                    width = 1.dp,
                    color = primary.copy(alpha = 0.6f),
                    shape = CircleShape,
                )
                .clip(CircleShape)
                .background(AnimaTheme.colors.surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(
                        bounded = true,
                        color = primary,
                    ),
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimaIcon(
                imageVector = LucideFingerprint,
                contentDescription = label,
                tint = primary,
                size = BiometricButtonDefaults.IconSize,
            )
        }

        Text(
            text = label,
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
        )
    }
}

object BiometricButtonDefaults {
    val Size = 72.dp
    val IconSize = 32.dp
}