package com.example.anima.features.profile.domain.model

// TODO implement?
//import com.anima.features.event.models.Event
//
//data class ExhibitorContact(
//    val link: String,
//    val phone: String,
//)

// the signed in user, the account type is not here, it comes from the session token
data class UserProfile(
    val name: String,
    val email: String,
    // ISO-8601 date time
    val registerDate: String,
    // TODO implement?
    // val document: String,
    // val recoveryEmail: String,
    // val isVerified: Boolean = false,
    // exhibitor only, null for a visitor
    // val contact: ExhibitorContact? = null,
    // val createdEvents: List<Event> = emptyList(),
    // val createdEventCount: Int = 0,
)
