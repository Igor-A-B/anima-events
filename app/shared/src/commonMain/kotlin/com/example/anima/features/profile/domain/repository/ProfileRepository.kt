package com.example.anima.features.profile.domain.repository

import com.example.anima.features.feed.domain.Event
import com.example.anima.features.profile.domain.model.UserProfile

// TODO: swap the implementation to go from mock to API
interface ProfileRepository {

    // the first page already comes inside UserProfile.createdEvents
    suspend fun getProfile(): UserProfile

    // the rest, loaded when the user taps see all
    suspend fun getAllCreatedEvents(): List<Event>
}
