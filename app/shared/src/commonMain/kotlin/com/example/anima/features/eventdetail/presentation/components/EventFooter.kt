package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_participate
import com.example.anima.core.components.button.AnimaButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventFooter(
    onParticipate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimaButton(
        text = stringResource(Res.string.event_detail_participate),
        onClick = onParticipate,
        modifier = modifier
    )
}