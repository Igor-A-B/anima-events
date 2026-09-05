package com.example.anima.navigation

import kotlinx.serialization.Serializable

// type-safe routes
// each destination is a type, not a string
// the compiler checks the arguments, so there is no route to build by hand

@Serializable
data object AuthGraph

@Serializable
data object Login

@Serializable
data object Register

@Serializable
data object AppGraph

@Serializable
data object Home

@Serializable
data object Search

@Serializable
data object AddEvent

@Serializable
data object Profile

// arguments are constructor parameters
@Serializable
data class EventDetail(val eventId: String)
