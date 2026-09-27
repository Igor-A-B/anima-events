package com.example.anima.core.components.picker

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideClock
import com.example.anima.core.theme.AnimaTheme
import java.util.Calendar

@Composable
actual fun AnimaTimePicker(
    value: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier,
    placeholder: String?,
    enabled: Boolean,
    forceShow: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dialog = remember {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val formatted = "%02d:%02d".format(hour, minute)

                onTimeSelected(formatted)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),

            true,
        ).apply {
            setOnDismissListener { onDismiss() }
        }
    }

    LaunchedEffect(forceShow) {
        if (forceShow) dialog.show()
    }

    AnimaPickerField(
        value = value,
        placeholder = placeholder,
        enabled = enabled,
        modifier = modifier.clickable(enabled = enabled) { dialog.show() },
        leadingIcon = {
            AnimaIcon(
                imageVector = LucideClock,
                contentDescription = null,
                tint = AnimaTheme.colors.onSurfaceVariant,
                size = AnimaTheme.spacing.lg,
            )
        },
    )
}