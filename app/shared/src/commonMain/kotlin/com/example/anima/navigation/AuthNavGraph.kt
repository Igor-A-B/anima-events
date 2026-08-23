package com.example.anima.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppRoutes.LOGIN,
        route = AppRoutes.AUTH_GRAPH,
    ) {
        composable(AppRoutes.LOGIN) {
            // login screen
        }

        composable(AppRoutes.REGISTER) {
            // register screen
        }
    }
}