package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.TokenResponseDto
import com.russhwolf.settings.Settings

// TODO: encrypt, this is plain shared preferences on android
class SettingsTokenStorage(private val settings: Settings = Settings()) : TokenStorage {

    override fun load(): TokenResponseDto? {
        val access = settings.getStringOrNull(ACCESS) ?: return null
        val refresh = settings.getStringOrNull(REFRESH) ?: return null
        return TokenResponseDto(access, refresh)
    }

    override fun save(tokens: TokenResponseDto) {
        settings.putString(ACCESS, tokens.accessToken)
        settings.putString(REFRESH, tokens.refreshToken)
    }

    override fun clear() {
        settings.remove(ACCESS)
        settings.remove(REFRESH)
    }

    private companion object {
        const val ACCESS = "access_token"
        const val REFRESH = "refresh_token"
    }
}
