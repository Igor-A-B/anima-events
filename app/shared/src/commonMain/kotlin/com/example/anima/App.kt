package com.example.anima

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.navigation.AppNavHost
import com.example.anima.di.appModule
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.navigation.AppGraph
import com.example.anima.navigation.AuthGraph
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        AnimaTheme {
            val navController = rememberNavController()
            // a saved session skips the login
            val sessions = koinInject<SessionRepository>()
            val signedIn = remember { sessions.session.value != null }

            AppNavHost(
                navController = navController,
                startDestination = if (signedIn) AppGraph else AuthGraph,
            )
        }
    }
}