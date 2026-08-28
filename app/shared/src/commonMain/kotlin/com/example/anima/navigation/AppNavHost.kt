package com.example.anima.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.anima.core.theme.AnimaTheme

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(AnimaTheme.colors.background),
        enterTransition = {
            slideInHorizontally { it } + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally { -it } + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideInHorizontally { -it } + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally { it } + fadeOut(tween(300))
        },
    ) {
        authNavGraph(navController)
        appNavGraph(navController)
    }
}