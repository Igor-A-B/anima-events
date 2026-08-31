package com.example.anima

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.navigation.AppNavHost
import com.example.anima.navigation.AuthGraph

@Composable
fun App() {
    AnimaTheme {
        val navController = rememberNavController()

        AppNavHost(
            navController = navController,
            startDestination = AuthGraph,
        )
    }
}