package com.example.anima.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data class AnimaShapes(
    val none: RoundedCornerShape = RoundedCornerShape(0.dp),
    val extraSmall: RoundedCornerShape = RoundedCornerShape(4.dp),
    val small: RoundedCornerShape = RoundedCornerShape(8.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(12.dp),
    val large: RoundedCornerShape = RoundedCornerShape(16.dp),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(24.dp),
    val full: RoundedCornerShape = RoundedCornerShape(50),
)

val LocalAnimaShapes = staticCompositionLocalOf { AnimaShapes() }