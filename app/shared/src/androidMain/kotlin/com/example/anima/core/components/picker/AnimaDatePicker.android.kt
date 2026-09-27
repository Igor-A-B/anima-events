package com.example.anima.core.components.picker

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCalendar
import com.example.anima.core.theme.AnimaTheme
import java.util.Calendar

@Composable
actual fun AnimaDatePicker(
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier,
    placeholder: String?,
    enabled: Boolean,
    forceShow: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = "%02d/%02d/%04d".format(day, month + 1, year)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
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
                imageVector = LucideCalendar,
                contentDescription = null,
                tint = AnimaTheme.colors.onSurfaceVariant,
                size = AnimaTheme.spacing.lg,
            )
        },
    )
}