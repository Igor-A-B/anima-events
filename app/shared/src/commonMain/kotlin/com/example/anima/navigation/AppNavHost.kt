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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anima.features.user.models.AccountType
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.navigation.bottomnav.AnimaBottomNav
import com.example.anima.navigation.bottomnav.BottomNavItem
import org.koin.compose.koinInject
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.anima.navigation.bottomnav.AnimaBottomNavDefaults

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // only exhibitors can create events
    val session by koinInject<SessionRepository>().session.collectAsStateWithLifecycle()
    val navItems = BottomNavItem.entries.filter {
        it != BottomNavItem.ADD_EVENT || session?.accountType == AccountType.EXHIBITOR
    }

    val showBottomNav = currentRoute in listOf(
        Home::class.qualifiedName,
        Search::class.qualifiedName,
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
                items = navItems,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = AnimaBottomNavDefaults.Gap),
            )
        }
    }
}