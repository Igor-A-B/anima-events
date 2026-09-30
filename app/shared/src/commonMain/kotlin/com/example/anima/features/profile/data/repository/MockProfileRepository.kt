package com.example.anima.features.profile.data.repository

import com.example.anima.features.profile.domain.model.UserProfile
import com.example.anima.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.delay

// fake data, no longer wired in AppModule, kept for previews and tests
// TODO implement? the mock exhibitor events and contact went away with the fields on UserProfile
class MockProfileRepository : ProfileRepository {

    override suspend fun getProfile(): UserProfile {
        // fake network latency
        delay(600)

        return UserProfile(
            name = "John Doe",
            email = "john.doe@gmail.com",
            registerDate = "2026-01-01T12:00:00",
        )
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) {
        delay(600)
    }
}
