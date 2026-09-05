package com.example.anima.features.auth.presentation.register.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.register_step5_subtitle
import anima.app.shared.generated.resources.register_step5_title
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCheck
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuccessStep(
    modifier: Modifier = Modifier,
) {
    val primary = AnimaTheme.colors.primary

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primary.copy(alpha = 0.9f),
                                primary.copy(alpha = 0.2f),
                            ),
                            radius = size.minDimension * 0.8f,
                        ),
                        radius = size.minDimension * 0.8f,
                    )
                }
                .clip(AnimaTheme.shapes.full)
                .background(AnimaTheme.colors.primary)
                .border(
                    width = 1.dp,
                    color = primary.copy(alpha = 0.6f),
                    shape = AnimaTheme.shapes.full,
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimaIcon(
                imageVector = LucideCheck,
                contentDescription = null,
                tint = AnimaTheme.colors.background,
                size = 40.dp,
            )
        }

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.xxl))

        Text(
            text = stringResource(Res.string.register_step5_title),
            style = AnimaTheme.typography.headlineMedium,
            color = AnimaTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.sm))

        Text(
            text = stringResource(Res.string.register_step5_subtitle),
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}