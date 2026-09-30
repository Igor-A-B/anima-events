package com.example.anima.features.profile.presentation

import com.anima.features.user.models.AccountType
import com.example.anima.core.error.AppError
import com.example.anima.features.profile.domain.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    // from the access token, not from the profile
    val accountType: AccountType = AccountType.VISITOR,
    val isLoading: Boolean = true,
    val error: String = "",
    val isPasswordDialogOpen: Boolean = false,
    val isChangingPassword: Boolean = false,
    val passwordError: AppError? = null,
    val isSigningOut: Boolean = false,
    // TODO implement?
    // the document is masked until the user asks to see it
    // val isDocumentVisible: Boolean = false,
    // true after see all pulled the remaining events
    // val isShowingAllEvents: Boolean = false,
    // val isLoadingEvents: Boolean = false,
) {
    val isExhibitor: Boolean = accountType == AccountType.EXHIBITOR

    // TODO implement?
    // val hasMoreEvents: Boolean = profile != null &&
    //     !isShowingAllEvents &&
    //     profile.createdEventCount > profile.createdEvents.size
}
