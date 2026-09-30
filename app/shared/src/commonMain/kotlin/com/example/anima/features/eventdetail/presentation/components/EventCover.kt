package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_free
import com.example.anima.core.components.button.AnimaIconButton
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideArrowLeft
import com.example.anima.core.components.icon.lucide.LucideHeart
import com.example.anima.core.components.icon.lucide.LucideShare
import com.example.anima.core.theme.AnimaTheme
import com.anima.features.event.models.EventCategory
import com.example.anima.features.feed.presentation.components.label
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

    val backgroundColor = AnimaTheme.colors.background

    // smooth scrim gradient to avoid color bleeding
    val bottomScrimBrush = remember(backgroundColor) {
        Brush.verticalGradient(
            0.00f to Color.Transparent,
            0.30f to backgroundColor.copy(alpha = 0.08f),
            0.55f to backgroundColor.copy(alpha = 0.28f),
            0.75f to backgroundColor.copy(alpha = 0.62f),
            0.90f to backgroundColor.copy(alpha = 0.88f),
            1.00f to backgroundColor,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(brush = Brush.linearGradient(colors = colors)),
    ) {
        // top scrim - buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // bottom scrim — title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.BottomCenter)
                .background(bottomScrimBrush),
        )

        // top buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AnimaTheme.spacing.xl,
                    vertical = AnimaTheme.spacing.lg,
                )
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimaIconButton(
                onClick = onBack,
                backgroundColor = Color.Black.copy(alpha = 0.3f),
            ) {
                AnimaIcon(
                    imageVector = LucideArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)) {
                AnimaIconButton(
                    onClick = onShare,
                    backgroundColor = Color.Black.copy(alpha = 0.3f),
                ) {
                    AnimaIcon(
                        imageVector = LucideShare,
                        contentDescription = null,
                        tint = Color.White,

                        )

                }

                AnimaIconButton(
                    onClick = onFavorite,
                    backgroundColor = Color.Black.copy(alpha = 0.3f),
                ) {
                    AnimaIcon(
                        imageVector = LucideHeart,
                        contentDescription = null,
                        tint = Color.White,
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
                .padding(bottom = AnimaTheme.spacing.lg),
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
private fun EventCategoryBadge(
    category: EventCategory,
    modifier: Modifier = Modifier,
) {
    Text(
        text = category.label(),
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