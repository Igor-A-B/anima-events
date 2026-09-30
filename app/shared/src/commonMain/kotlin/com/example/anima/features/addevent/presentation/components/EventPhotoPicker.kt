package com.example.anima.features.addevent.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_photo_picker
import anima.app.shared.generated.resources.add_event_photo_remove
import anima.app.shared.generated.resources.add_event_photo_replace
import anima.app.shared.generated.resources.add_event_photo_take
import coil3.compose.AsyncImage
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.image.PickedImage
import com.example.anima.core.components.icon.lucide.LucideImage
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventPhotoPicker(
    photo: PickedImage?,
    onPickPhoto: () -> Unit,
    // null where the platform can't take photos
    onTakePhoto: (() -> Unit)?,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (photo != null) {
        PhotoPreview(
            photo = photo,
            onReplace = onPickPhoto,
            onTakePhoto = onTakePhoto,
            onRemove = onRemovePhoto,
            modifier = modifier,
        )
        return
    }

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

        if (onTakePhoto != null) {
            Text(
                text = stringResource(Res.string.add_event_photo_take),
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.primary,
                modifier = Modifier
                    .clickable { onTakePhoto() }
                    .padding(AnimaTheme.spacing.sm),
            )
        }
    }
}

@Composable
private fun PhotoPreview(
    photo: PickedImage,
    onReplace: () -> Unit,
    onTakePhoto: (() -> Unit)?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(AnimaTheme.shapes.large)
                .background(AnimaTheme.colors.surface),
        ) {
            AsyncImage(
                model = photo.bytes,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.lg)) {
            PhotoAction(stringResource(Res.string.add_event_photo_replace), onReplace)
            if (onTakePhoto != null) {
                PhotoAction(stringResource(Res.string.add_event_photo_take), onTakePhoto)
            }
            PhotoAction(stringResource(Res.string.add_event_photo_remove), onRemove)
        }
    }
}

@Composable
private fun PhotoAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = AnimaTheme.typography.bodyMedium,
        color = AnimaTheme.colors.primary,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = AnimaTheme.spacing.xs),
    )
}