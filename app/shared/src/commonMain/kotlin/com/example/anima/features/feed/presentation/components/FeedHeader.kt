package com.example.anima.features.feed.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.feed_headline
import anima.app.shared.generated.resources.home_welcome_message
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeedHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
    ) {
        Text(
            text = stringResource(Res.string.home_welcome_message),
            style = AnimaTheme.typography.bodyMedium,
            color = AnimaTheme.colors.onSurfaceVariant,
        )
        Text(
            text = stringResource(Res.string.feed_headline),
            style = AnimaTheme.typography.headlineSmall,
            color = AnimaTheme.colors.onBackground,
        )
    }
}
