package com.example.anima.features.eventdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_received_id
import anima.app.shared.generated.resources.event_detail_title
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideArrowLeft
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

// placeholder: proves the card click navigates and the id arrives
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit = {},
) {
    AnimaScaffold {
        Column(
            modifier = Modifier
                .background(AnimaTheme.colors.background)
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(AnimaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.lg),
        ) {
            Box(modifier = Modifier.clickable { onNavigateBack() }) {
                AnimaIcon(
                    imageVector = LucideArrowLeft,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onBackground,
                )
            }

            Text(
                text = stringResource(Res.string.event_detail_title),
                style = AnimaTheme.typography.headlineSmall,
                color = AnimaTheme.colors.onBackground,
            )

            Text(
                text = stringResource(Res.string.event_detail_received_id, eventId),
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
            )
        }
    }
}
