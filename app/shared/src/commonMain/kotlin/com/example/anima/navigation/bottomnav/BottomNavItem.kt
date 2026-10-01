package com.example.anima.navigation.bottomnav

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
import kotlin.reflect.KClass

enum class BottomNavItem(
    val route: Any,
    val routeClass: KClass<out Any>,
    val icon: @Composable () -> ImageVector,
) {
    FEED(
        route = Home,
        routeClass = Home::class,
        icon = { LucideHome },
    ),
    SEARCH(
        route = Search,
        routeClass = Search::class,
        icon = { LucideSearch },
    ),
    ADD_EVENT(
        route = AddEvent,
        routeClass = AddEvent::class,
        icon = { LucideCirclePlus },
    ),
    PROFILE(
        route = Profile,
        routeClass = Profile::class,
        icon = { LucideCircleUser },
    ),
}