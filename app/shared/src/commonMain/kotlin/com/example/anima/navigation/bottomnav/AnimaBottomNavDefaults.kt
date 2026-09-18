package com.example.anima.navigation.bottomnav

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.example.anima.core.components.button.AnimaButtonDefaults
import com.example.anima.core.theme.AnimaTheme

object AnimaBottomNavDefaults {

    // gap between the system bar and the floating pill
    val Gap: Dp @Composable get() = AnimaTheme.spacing.lg

    val Height: Dp = AnimaButtonDefaults.Height

    // space a scrolling screen reserves so its last row clears the pill
    val ContentReserve: Dp
        @Composable get() = WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding() + Gap + Height + AnimaTheme.spacing.xl
}