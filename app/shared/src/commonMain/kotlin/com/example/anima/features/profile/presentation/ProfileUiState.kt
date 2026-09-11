package com.example.anima.features.profile.presentation

import com.example.anima.features.profile.domain.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    // the document is masked until the user asks to see it
    val isDocumentVisible: Boolean = false,
    // true after see all pulled the remaining events
    val isShowingAllEvents: Boolean = false,
    val isLoadingEvents: Boolean = false,
    val error: String = "",
) {
    val hasMoreEvents: Boolean = profile != null &&
        !isShowingAllEvents &&
        profile.createdEventCount > profile.createdEvents.size
}
