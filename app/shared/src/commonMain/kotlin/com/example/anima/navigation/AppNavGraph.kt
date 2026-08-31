package com.example.anima.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.anima.features.eventdetail.presentation.EventDetailScreen
import com.example.anima.features.feed.presentation.FeedScreen

fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    navigation<AppGraph>(
        startDestination = Home,
        enterTransition = NavTransitions.enter,
        exitTransition = NavTransitions.exit,
        popEnterTransition = NavTransitions.popEnter,
        popExitTransition = NavTransitions.popExit
    ) {

        composable<Home> {
            FeedScreen(
                onNavigateToEvent = { eventId ->
                    navController.navigate(EventDetail(eventId))
                },
            )
        }

        composable<EventDetail> { backStackEntry ->
            val route: EventDetail = backStackEntry.toRoute()

            EventDetailScreen(
                eventId = route.eventId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}