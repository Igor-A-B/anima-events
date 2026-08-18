package com.example.anima.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Classe que diz qual a função de cada cor
private val DarkAppColorScheme = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceDark,
    primary = AccentMagenta,
    onPrimary = IconDark,
    onBackground = onBackground,
)

@Composable
fun AnimaTheme(
    content: @Composable () -> Unit
) {
    // Futuramente light mode vai aqui
    MaterialTheme(
        colorScheme = DarkAppColorScheme,
        content = content
    )
}