package com.example.anima.features.auth.data

import com.russhwolf.settings.Settings
import com.anima.features.auth.dtos.TokenResponseDto

// keeps the tokens between app launches
// TODO: encrypt, this is plain shared preferences on android
class TokenStorage(private val settings: Settings = Settings()) {

    fun load(): TokenResponseDto? {
        val access = settings.getStringOrNull(ACCESS) ?: return null
        val refresh = settings.getStringOrNull(REFRESH) ?: return null
        return TokenResponseDto(access, refresh)
    }

    fun save(tokens: TokenResponseDto) {
        settings.putString(ACCESS, tokens.accessToken)
        settings.putString(REFRESH, tokens.refreshToken)
    }

    fun clear() {
        settings.remove(ACCESS)
        settings.remove(REFRESH)
    }

    private companion object {
        const val ACCESS = "access_token"
        const val REFRESH = "refresh_token"
    }
}
