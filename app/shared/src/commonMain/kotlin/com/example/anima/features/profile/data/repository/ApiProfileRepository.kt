package com.example.anima.features.profile.data.repository

import com.anima.features.user.dtos.ChangePasswordRequestDto
import com.anima.features.user.dtos.MeResponseDto
import com.example.anima.features.profile.domain.model.UserProfile
import com.example.anima.features.profile.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import com.example.anima.core.network.jsonBody

class ApiProfileRepository(private val client: HttpClient) : ProfileRepository {

    override suspend fun getProfile(): UserProfile {
        val me = client.get("me").body<MeResponseDto>()
        return UserProfile(name = me.name, email = me.email, registerDate = me.registerDate)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) {
        client.post("me/password") { jsonBody(ChangePasswordRequestDto(currentPassword, newPassword)) }
    }
}
