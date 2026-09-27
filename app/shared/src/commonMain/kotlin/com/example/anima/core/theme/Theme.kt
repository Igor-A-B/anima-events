package com.example.anima.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun AnimaTheme(
    // identify the current theme based on the system
    darkTheme: Boolean = isSystemInDarkTheme(),
    // define colors based on theme
    colors: AnimaColors = if (darkTheme) darkAnimaColors else lightAnimaColors,
    content: @Composable () -> Unit
) {
    // must be created inside a @Composable cause Font(Res.font.*)
    // requires a composable context to load composeResources in runtime
    val typography = animaTypography()

    // pushes all token sets down in the composition tree
    // so any child can read them without needing them as parameters
    CompositionLocalProvider(
        LocalAnimaColors provides colors,
        LocalAnimaTypography provides typography,
        LocalAnimaShapes provides AnimaShapes(),
        LocalAnimaSpacing provides AnimaSpacing(),
        content = content,
    )
}

// singleton accessor - mirrors MaterialTheme API so call sites are familiar
object AnimaTheme {
    val colors: AnimaColors
        @Composable @ReadOnlyComposable get() = LocalAnimaColors.current

    val typography: AnimaTypography
        @Composable @ReadOnlyComposable get() = LocalAnimaTypography.current

    val shapes: AnimaShapes
        @Composable @ReadOnlyComposable get() = LocalAnimaShapes.current

    val spacing: AnimaSpacing
        @Composable @ReadOnlyComposable get() = LocalAnimaSpacing.current
}