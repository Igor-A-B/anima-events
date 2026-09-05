package com.example.anima.navigation.bottom_nav

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.anima.core.components.button.AnimaButtonDefaults
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.theme.AnimaTheme

@Composable
fun AnimaBottomNav(
    currentRoute: Any?,
    onItemClick: (Any) -> Unit,
    items: List<BottomNavItem> = BottomNavItem.entries,
    modifier: Modifier = Modifier,
) {
    val shape = AnimaTheme.shapes.full

    Box(
        modifier = modifier
            .padding(horizontal = AnimaTheme.spacing.xl)
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = AnimaTheme.colors.primary.copy(alpha = 0.1f),
                spotColor = AnimaTheme.colors.primary.copy(alpha = 0.1f),
            )
            .clip(shape)
            .background(AnimaTheme.colors.surface.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = AnimaTheme.colors.outline.copy(alpha = 0.2f),
                shape = shape,
            )
            .height(AnimaButtonDefaults.Height)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AnimaTheme.spacing.xl),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.qualifiedName

                val tint by animateColorAsState(
                    targetValue = if (isSelected) {
                        AnimaTheme.colors.onPrimary
                    } else {
                        AnimaTheme.colors.onSurfaceVariant
                    },
                    animationSpec = tween(200),
                    label = "BottomNavTint",
                )

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        AnimaTheme.colors.primary
                    } else {
                        Color.Transparent
                    },
                    animationSpec = tween(200),
                    label = "BottomNavBackground",
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(AnimaTheme.shapes.extraLarge)
                        .background(backgroundColor)
                        .clickable { onItemClick(item.route) },
                    contentAlignment = Alignment.Center,
                ) {
                    AnimaIcon(
                        imageVector = item.icon(),
                        contentDescription = null,
                        tint = tint,
                        size = 30.dp,
                    )
                }
            }
        }
    }
}