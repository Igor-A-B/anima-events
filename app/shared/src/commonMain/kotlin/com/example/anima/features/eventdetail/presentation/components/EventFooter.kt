package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_subscription_error
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.button.AnimaButtonVariant
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.eventdetail.presentation.SubscriptionUi
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventFooter(
    state: SubscriptionUi,
    hasError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        if (hasError) {
            Text(
                text = stringResource(Res.string.event_detail_subscription_error),
                style = AnimaTheme.typography.bodySmall,
                color = AnimaTheme.colors.error,
                textAlign = TextAlign.Center,
            )
        }

        AnimaButton(
            text = state.label(),
            onClick = onClick,
            variant = if (state == SubscriptionUi.Confirmed) AnimaButtonVariant.OUTLINED else AnimaButtonVariant.PRIMARY,
            enabled = state != SubscriptionUi.Attended,
            loading = state == SubscriptionUi.Loading,
        )
    }
}
