package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.anima.core.error.AppError
import com.example.anima.core.error.messageRes
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.button.AnimaButtonVariant
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource
import com.example.anima.features.eventdetail.presentation.SubscriptionUi

@Composable
fun EventFooter(
    state: SubscriptionUi,
    error: AppError?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        if (error != null) {
            Text(
                text = stringResource(error.messageRes()),
                style = AnimaTheme.typography.bodySmall,
                color = AnimaTheme.colors.error,
                textAlign = TextAlign.Center,
            )
        }

        AnimaButton(
            text = state.label(),
            onClick = onClick,
            variant = if (state == SubscriptionUi.Confirmed) AnimaButtonVariant.OUTLINED else AnimaButtonVariant.PRIMARY,
            enabled = state != SubscriptionUi.Attended && state != SubscriptionUi.Full,
            loading = state == SubscriptionUi.Loading,
        )
    }
}
