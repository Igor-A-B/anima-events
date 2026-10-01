package com.example.anima.core.components.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCheck
import com.example.anima.core.components.icon.lucide.LucideCircle
import com.example.anima.core.components.icon.lucide.LucideClock
import com.example.anima.core.components.icon.lucide.LucideShieldAlert
import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.error.Severity
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.getString

// listens to the bus, so any AppException reported anywhere shows up here
@Composable
fun AnimaSnackbarHost(bus: AppExceptionBus, modifier: Modifier = Modifier) {
    val hostState = remember { SnackbarHostState() }
    var severity by remember { mutableStateOf(Severity.ERROR) }

    LaunchedEffect(bus) {
        bus.exceptions.collect { exception ->
            severity = exception.severity
            // a new message replaces the current one
            hostState.currentSnackbarData?.dismiss()
            hostState.showSnackbar(
                message = getString(exception.messageRes),
                duration = exception.severity.duration(),
            )
        }
    }

    SnackbarHost(
        hostState = hostState,
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) { data ->
        val colors = severity.colors()
        Row(
            modifier = Modifier
                .padding(AnimaTheme.spacing.lg)
                .fillMaxWidth()
                .background(colors.container, AnimaTheme.shapes.medium)
                .padding(AnimaTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimaIcon(severity.icon(), tint = colors.content)
            Text(
                text = data.visuals.message,
                style = AnimaTheme.typography.bodyMedium,
                color = colors.content,
                modifier = Modifier.padding(start = AnimaTheme.spacing.md),
            )
        }
    }
}

// the snackbar is temporary, only FATAL stays longer
private fun Severity.duration() = when (this) {
    Severity.FATAL -> SnackbarDuration.Long
    else -> SnackbarDuration.Short
}

@Composable
private fun Severity.icon(): ImageVector = when (this) {
    Severity.ERROR, Severity.FATAL, Severity.WARN -> LucideShieldAlert
    Severity.SUCCESS -> LucideCheck
    Severity.INFO -> LucideClock
    Severity.NOTIFICATION -> LucideCircle
}

private class SnackbarColors(val container: Color, val content: Color)

@Composable
private fun Severity.colors(): SnackbarColors {
    val c = AnimaTheme.colors
    return when (this) {
        Severity.ERROR -> SnackbarColors(c.error, c.onError)
        Severity.FATAL -> SnackbarColors(c.primary, c.onPrimary)
        Severity.WARN -> SnackbarColors(c.warning, c.background)
        Severity.SUCCESS -> SnackbarColors(c.success, c.background)
        Severity.INFO, Severity.NOTIFICATION -> SnackbarColors(c.surfaceVariant, c.onSurface)
    }
}
