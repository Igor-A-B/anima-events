package com.example.anima.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.navigation.bottom_nav.AnimaBottomNav

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute in listOf(
        Home::class.qualifiedName,
        Search::class.qualifiedName,
        AddEvent::class.qualifiedName,
        Profile::class.qualifiedName,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimaTheme.colors.background),
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { slideInHorizontally { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally { -it } + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn(tween(300)) },
            popExitTransition = { slideOutHorizontally { it } + fadeOut(tween(300)) },
        ) {
            authNavGraph(navController)
            appNavGraph(navController)
        }

        if (showBottomNav) {
            AnimaBottomNav(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AnimaTheme.spacing.xxl),
            )
        }
    }
}