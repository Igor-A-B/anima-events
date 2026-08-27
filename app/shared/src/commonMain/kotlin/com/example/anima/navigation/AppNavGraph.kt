package com.example.anima.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.anima.features.eventdetail.presentation.EventDetailScreen
import com.example.anima.features.feed.presentation.FeedScreen

fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    navigation(
        startDestination = AppRoutes.HOME,
        route = AppRoutes.APP_GRAPH,
    ) {
        composable(AppRoutes.HOME) {
            FeedScreen(
                onNavigateToEvent = { eventId ->
                    navController.navigate(AppRoutes.eventDetail(eventId))
                },
            )
        }

        composable(
            route = AppRoutes.EVENT_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.EVENT_ID_ARG) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments
                ?.getString(AppRoutes.EVENT_ID_ARG)
                .orEmpty()

            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

    }
}
