package com.example.anima.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// primary colors
val Primary50 = Color(0xFFFFF0F2)
val Primary100 = Color(0xFFFFDDE1)
val Primary200 = Color(0xFFFFBEC5)
val Primary300 = Color(0xFFFF8F9D)
val Primary400 = Color(0xFFFCCED3)
val Primary500 = Color(0xFFFFB2BB)
val Primary600 = Color(0xFFE8476A)
val Primary700 = Color(0xFFD42D54)
val Primary800 = Color(0xFF3B1D23)
val Primary900 = Color(0xFF2A1219)
val Primary = Color(0xFFBC1F4B)

// neutral colors
val Neutral50 = Color(0xFFF8F8F8)
val Neutral100 = Color(0xFFFFECED)
val Neutral200 = Color(0xFFF7DCDE)
val Neutral300 = Color(0xFFDAC0C2)
val Neutral400 = Color(0xFFBEA5A7)
val Neutral500 = Color(0xFFA28B8D)
val Neutral600 = Color(0xFF877274)
val Neutral700 = Color(0xFF6D595B)
val Neutral800 = Color(0xFF332D2D)
val Neutral900 = Color(0xFF1C1A1A)

data class AnimaColors(
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,
    val outline: Color,
    val error: Color,
    val onError: Color,
)

val darkAnimaColors = AnimaColors(
    background = Neutral900,
    onBackground = Neutral100,
    surface = Neutral800,
    surfaceVariant = Primary900,
    onSurface = Neutral100,
    onSurfaceVariant = Neutral500,
    primary = Primary,
    primaryVariant = Primary600,
    onPrimary = Neutral50,
    outline = Neutral600,
    error = Primary300,
    onError = Primary900,
)

val lightAnimaColors = AnimaColors(
    background = Neutral100,
    onBackground = Neutral900,
    surface = Neutral100,
    surfaceVariant = Primary100,
    onSurface = Neutral800,
    onSurfaceVariant = Neutral600,
    primary = Primary,
    primaryVariant = Primary700,
    onPrimary = Neutral50,
    outline = Neutral300,
    error = Primary600,
    onError = Neutral50,
)

val LocalAnimaColors = staticCompositionLocalOf<AnimaColors> {
    error("No AnimaColors provided")
}