package com.example.anima

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.anima.core.components.snackbar.AnimaSnackbarHost
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
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
    // event images are public urls, loaded with coil's own ktor client (not the api one, no token, no base url)
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
    }
    KoinApplication(application = { modules(appModule) }) {
        AnimaTheme {
            val navController = rememberNavController()
            // a saved session skips the login
            val sessions = koinInject<SessionRepository>()
            val signedIn = remember { sessions.session.value != null }

            Box {
                AppNavHost(
                    navController = navController,
                    startDestination = if (signedIn) AppGraph else AuthGraph,
                )
                // global, shows every AppException reported on the bus
                AnimaSnackbarHost(
                    bus = koinInject(),
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
    }
}