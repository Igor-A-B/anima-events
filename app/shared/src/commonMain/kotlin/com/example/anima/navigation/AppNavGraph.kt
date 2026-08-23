package com.example.anima.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppRoutes.HOME,
        route = AppRoutes.APP_GRAPH,
    ) {
        composable(AppRoutes.HOME) {
            // home screen
        }

        // other app screens
    }
}