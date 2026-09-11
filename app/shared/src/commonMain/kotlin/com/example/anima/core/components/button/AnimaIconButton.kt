package com.example.anima.core.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.anima.core.theme.AnimaTheme

@Composable
fun AnimaIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = AnimaIconButtonDefaults.Size,
    backgroundColor: Color = AnimaTheme.colors.surface,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(AnimaTheme.shapes.full)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

object AnimaIconButtonDefaults {
    val Size: Dp = 40.dp
}