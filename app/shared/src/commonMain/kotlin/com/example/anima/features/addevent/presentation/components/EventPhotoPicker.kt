package com.example.anima.features.addevent.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_photo_picker
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideImage
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventPhotoPicker(
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = AnimaTheme.colors.outline
    val shape = AnimaTheme.shapes.large

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .background(AnimaTheme.colors.surface)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(12f, 8f),
                        ),
                    ),
                )
            }
            .clickable { onPickPhoto() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = AnimaTheme.spacing.sm,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        AnimaIcon(
            imageVector = LucideImage,
            contentDescription = null,
            tint = AnimaTheme.colors.onSurfaceVariant,
            size = 32.dp,
        )

        Text(
            text = stringResource(Res.string.add_event_photo_picker),
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
        )
    }
}