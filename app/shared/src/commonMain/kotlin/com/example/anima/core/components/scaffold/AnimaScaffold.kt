package com.example.anima.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.anima.core.theme.AnimaTheme

enum class AnimaScaffoldInsets {
    /** standalone screen — login, register, detail. consume all insets. */
    Standalone,

    /** chrome screens with bottom nav. only consumes top. */
    WithChrome,
}

@Composable
fun AnimaScaffold(
    modifier: Modifier = Modifier,
    insets: AnimaScaffoldInsets = AnimaScaffoldInsets.Standalone,
    content: @Composable BoxScope.() -> Unit,
) {
    val insetsModifier = when (insets) {
        AnimaScaffoldInsets.Standalone -> Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        AnimaScaffoldInsets.WithChrome -> Modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(
                WindowInsetsSides.Top
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AnimaTheme.colors.background)
            .then(insetsModifier),
        content = content,
    )
}