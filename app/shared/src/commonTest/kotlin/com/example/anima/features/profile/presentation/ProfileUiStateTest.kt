package com.example.anima.features.profile.presentation

import com.example.anima.core.error.AppError
import com.example.anima.features.profile.domain.model.UserProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfileUiStateTest {

    private val profile = UserProfile(name = "Ana", email = "ana@example.com", registerDate = "2026-01-02T03:04:05")

    @Test
    fun a_loaded_profile_clears_an_older_error() {
        val state = ProfileUiState().withProfileError(AppError.NETWORK).withProfile(profile)

        assertNull(state.error)
        assertFalse(state.isLoading)
        assertEquals(profile, state.profile)
    }

    @Test
    fun a_failure_keeps_the_reason() {
        val state = ProfileUiState().withProfileError(AppError.INVALID_CREDENTIALS)

        assertEquals(AppError.INVALID_CREDENTIALS, state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun a_retry_clears_the_error_and_shows_the_spinner_without_a_profile() {
        val state = ProfileUiState().withProfileError(AppError.NETWORK).loadingProfile()

        assertNull(state.error)
        assertTrue(state.isLoading)
    }

    @Test
    fun a_reload_keeps_the_shown_profile_on_screen() {
        val state = ProfileUiState().withProfile(profile).loadingProfile()

        assertFalse(state.isLoading)
        assertEquals(profile, state.profile)
    }
}
