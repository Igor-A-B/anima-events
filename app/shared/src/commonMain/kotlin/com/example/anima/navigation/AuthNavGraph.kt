package com.example.anima.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.anima.features.auth.presentation.login.LoginScreen
import com.example.anima.features.auth.presentation.register.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation<AuthGraph>(startDestination = Login) {
        composable<Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Register)
                },
                onLoginSuccess = {
                    navController.navigate(AppGraph) {
                        // clears the auth flow from the back stack
                        popUpTo<AuthGraph> { inclusive = true }
                    }
                },
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterComplete = {
                    navController.navigate(AppGraph) {
                        popUpTo<AuthGraph> { inclusive = true }
                    }
                },
            )
        }
    }
}
