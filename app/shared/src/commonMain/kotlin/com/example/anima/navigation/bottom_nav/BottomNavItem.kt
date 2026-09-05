package com.example.anima.navigation.bottom_nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.anima.core.components.icon.lucide.LucideCirclePlus
import com.example.anima.core.components.icon.lucide.LucideCircleUser
import com.example.anima.core.components.icon.lucide.LucideHome
import com.example.anima.core.components.icon.lucide.LucideSearch
import com.example.anima.navigation.AddEvent
import com.example.anima.navigation.Home
import com.example.anima.navigation.Profile
import com.example.anima.navigation.Search

enum class BottomNavItem(
    val route: Any,
    val qualifiedName: String?,
    val icon: @Composable () -> ImageVector,
) {
    FEED(
        route = Home,
        qualifiedName = Home::class.qualifiedName,
        icon = { LucideHome },
    ),
    SEARCH(
        route = Search,
        qualifiedName = Search::class.qualifiedName,
        icon = { LucideSearch },
    ),
    ADD_EVENT(
        route = AddEvent,
        qualifiedName = AddEvent::class.qualifiedName,
        icon = { LucideCirclePlus },
    ),
    PROFILE(
        route = Profile,
        qualifiedName = Profile::class.qualifiedName,
        icon = { LucideCircleUser },
    ),
}