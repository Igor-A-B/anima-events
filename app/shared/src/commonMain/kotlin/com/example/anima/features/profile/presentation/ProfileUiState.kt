package com.example.anima.features.profile.presentation

import com.anima.features.event.models.Event
import com.anima.features.user.models.AccountType
import com.example.anima.core.error.AppError
import com.example.anima.features.profile.domain.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    // from the access token, not from the profile
    val accountType: AccountType = AccountType.VISITOR,
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val isPasswordDialogOpen: Boolean = false,
    val isChangingPassword: Boolean = false,
    val passwordError: AppError? = null,
    val isSigningOut: Boolean = false,
    // exhibitor only, the events they organize
    val createdEvents: List<Event> = emptyList(),
    val isLoadingEvents: Boolean = false,
    // visitor only, the events they take part in
    val participatingEvents: List<Event> = emptyList(),
    // the last load of the events section failed, the section offers a retry
    val eventsError: AppError? = null,
    // TODO implement?
    // the document is masked until the user asks to see it
    // val isDocumentVisible: Boolean = false,
    // true after see all pulled the remaining events
    // val isShowingAllEvents: Boolean = false,
    // val isLoadingEvents: Boolean = false,
) {
    val isExhibitor: Boolean = accountType == AccountType.EXHIBITOR

    // hosted events for an exhibitor, joined events for a visitor
    val myEvents: List<Event> = if (isExhibitor) createdEvents else participatingEvents

    // TODO implement?
    // val hasMoreEvents: Boolean = profile != null &&
    //     !isShowingAllEvents &&
    //     profile.createdEventCount > profile.createdEvents.size
}
