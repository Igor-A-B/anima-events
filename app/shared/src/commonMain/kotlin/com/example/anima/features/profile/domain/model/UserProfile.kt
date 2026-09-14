package com.example.anima.features.profile.domain.model

import com.example.anima.features.auth.presentation.register.AccountType
import com.example.anima.features.feed.domain.Event

data class ExhibitorContact(
    val link: String,
    val phone: String,
)

// the signed in user
// TODO: real types when the API lands
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val document: String,
    val recoveryEmail: String,
    val accountType: AccountType,
    val isVerified: Boolean = false,
    // exhibitor only, null for a visitor
    val contact: ExhibitorContact? = null,
    val createdEvents: List<Event> = emptyList(),
    val createdEventCount: Int = 0,
) {
    val isExhibitor: Boolean = accountType == AccountType.EXHIBITOR
}
