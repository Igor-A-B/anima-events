package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_free
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideArrowLeft
import com.example.anima.core.components.icon.lucide.LucideHeart
import com.example.anima.core.components.icon.lucide.LucideShare
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.EventCategory
import org.jetbrains.compose.resources.stringResource

private val coverColors = listOf(
    listOf(Color(0xFF1A0A2E), Color(0xFF4B0082)),
    listOf(Color(0xFF0D1B2A), Color(0xFF1B4332)),
    listOf(Color(0xFF1A0000), Color(0xFF8B0000)),
    listOf(Color(0xFF0A0A2E), Color(0xFF00008B)),
    listOf(Color(0xFF1A1A0A), Color(0xFF556B2F)),
    listOf(Color(0xFF2A0A1A), Color(0xFF8B008B)),
)

@Composable
fun EventCover(
    coverSeed: Int,
    title: String,
    category: EventCategory,
    price: String?,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = coverColors[coverSeed % coverColors.size]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(brush = Brush.linearGradient(colors = colors)),
    ) {
        // top scrim - buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AnimaTheme.colors.background.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                    ),
                ),
        )

        // bottom scrim — title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AnimaTheme.colors.background.copy(alpha = 0.7f),
                            AnimaTheme.colors.background,
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    ),
                ),
        )

        // top buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AnimaTheme.spacing.xl, vertical = AnimaTheme.spacing.xl)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoverIconButton(onClick = onBack) {
                AnimaIcon(
                    imageVector = LucideArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                    size = 20.dp,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)) {
                CoverIconButton(onClick = onShare) {
                    AnimaIcon(
                        imageVector = LucideShare,
                        contentDescription = null,
                        tint = Color.White,
                        size = 20.dp,
                    )
                }
                CoverIconButton(onClick = onFavorite) {
                    AnimaIcon(
                        imageVector = LucideHeart,
                        contentDescription = null,
                        tint = Color.White,
                        size = 20.dp,
                    )
                }
            }

        }

        // title in bottom gradient
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = AnimaTheme.spacing.xl)
                .padding(bottom = AnimaTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
        ) {
            EventCategoryBadge(category = category)

            Text(
                text = title,
                style = AnimaTheme.typography.headlineMedium,
                color = AnimaTheme.colors.onBackground,
            )

            Text(
                text = price ?: stringResource(Res.string.event_detail_free),
                style = AnimaTheme.typography.titleMedium,
                color = if (price != null) AnimaTheme.colors.primary else AnimaTheme.colors.success,
            )
        }
    }
}

@Composable
private fun CoverIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.size(40.dp).clip(AnimaTheme.shapes.full)
            .background(Color.Black.copy(alpha = 0.3f)).clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun EventCategoryBadge(
    category: EventCategory,
    modifier: Modifier = Modifier,
) {
    Text(
        text = category.name.lowercase().replaceFirstChar { it.uppercase() },
        style = AnimaTheme.typography.labelSmall,
        color = AnimaTheme.colors.primary,
        modifier = modifier
            .background(
                color = AnimaTheme.colors.primary.copy(alpha = 0.15f),
                shape = AnimaTheme.shapes.full,
            )
            .padding(
                horizontal = AnimaTheme.spacing.md,
                vertical = AnimaTheme.spacing.xs,
            ),
    )
}