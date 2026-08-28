package com.example.anima.features.feed.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.anima.core.components.icon.lucide.LucideChefHat
import com.example.anima.core.components.icon.lucide.LucideClapperboard
import com.example.anima.core.components.icon.lucide.LucideCpu
import com.example.anima.core.components.icon.lucide.LucideMusic
import com.example.anima.core.components.icon.lucide.LucidePalette
import com.example.anima.core.components.icon.lucide.LucidePartyPopper
import com.example.anima.core.components.icon.lucide.LucideSport
import com.example.anima.core.components.icon.lucide.LucideTheater
import com.example.anima.features.feed.domain.EventCategory

// map the domain enum to an icon, so the domain stays free of compose
val EventCategory.icon: ImageVector
    @Composable get() = when (this) {
        EventCategory.MUSIC -> LucideMusic
        EventCategory.ARTS -> LucidePalette
        EventCategory.CUISINE -> LucideChefHat
        EventCategory.PARTY -> LucidePartyPopper
        EventCategory.SPORT -> LucideSport
        EventCategory.THEATER -> LucideTheater
        EventCategory.CINEMA -> LucideClapperboard
        EventCategory.TECH -> LucideCpu
    }
