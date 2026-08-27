package com.example.anima.navigation

object AppRoutes {
    const val AUTH_GRAPH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val APP_GRAPH = "app"
    const val HOME = "home"

    // route with argument, read from the NavBackStackEntry
    const val EVENT_ID_ARG = "eventId"
    const val EVENT_DETAIL = "event/{$EVENT_ID_ARG}"

    // builds the concrete route when navigating
    fun eventDetail(eventId: String) = "event/$eventId"
}
