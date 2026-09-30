package com.example.anima.features.addevent.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_delete
import anima.app.shared.generated.resources.add_event_delete_confirm_text
import anima.app.shared.generated.resources.add_event_delete_confirm_title
import anima.app.shared.generated.resources.core_button_cancel
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteEventDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AnimaTheme.colors.surface,
        title = {
            Text(
                text = stringResource(Res.string.add_event_delete_confirm_title),
                style = AnimaTheme.typography.titleLarge,
                color = AnimaTheme.colors.onSurface,
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.add_event_delete_confirm_text),
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isDeleting) {
                Text(text = stringResource(Res.string.add_event_delete), color = AnimaTheme.colors.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) {
                Text(text = stringResource(Res.string.core_button_cancel), color = AnimaTheme.colors.onSurfaceVariant)
            }
        },
    )
}
