package com.example.anima.features.profile.domain.repository

import com.example.anima.features.profile.domain.model.UserProfile

// both throw ApiException on failure
interface ProfileRepository {

    suspend fun getProfile(): UserProfile

    // 403 means the current password is wrong, 400 means the new one is invalid
    suspend fun changePassword(currentPassword: String, newPassword: String)

    // TODO implement?
    // the rest of the created events, loaded when the user taps see all
    // suspend fun getAllCreatedEvents(): List<Event>
}
