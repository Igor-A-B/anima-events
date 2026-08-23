package com.example.anima.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.anima.features.auth.presentation.login.LoginScreen
import com.example.anima.features.auth.presentation.register.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppRoutes.LOGIN,
        route = AppRoutes.AUTH_GRAPH,
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                },
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterComplete = {
                    navController.navigate(AppRoutes.APP_GRAPH) {
                        popUpTo(AppRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                },
            )
        }
    }
}