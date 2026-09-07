package com.example.anima.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.anima.features.eventdetail.presentation.EventDetailScreen
import com.example.anima.features.feed.presentation.FeedScreen
import com.example.anima.features.search.presentation.SearchScreen

fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    navigation<AppGraph>(startDestination = Home) {
        composable<Home> {
            FeedScreen(
                onNavigateToEvent = { eventId ->
                    navController.navigate(EventDetail(eventId))
                },
            )
        }

        composable<Search> {
            SearchScreen(
                onNavigateToEvent = { eventId ->
                    navController.navigate(EventDetail(eventId))
                },
            )
        }

        composable<AddEvent> {
            // AddEventScreen()
        }

        composable<Profile> {
            // ProfileScreen()
        }

        composable<EventDetail>(
            enterTransition = { slideInHorizontally { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally { it } + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn(tween(300)) },
            popExitTransition = { slideOutHorizontally { it } + fadeOut(tween(300)) },
        ) { backStackEntry ->
            val route: EventDetail = backStackEntry.toRoute()
            EventDetailScreen(
                eventId = route.eventId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
