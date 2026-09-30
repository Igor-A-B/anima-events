package com.example.anima.features.auth.data

import com.anima.features.auth.dtos.TokenResponseDto

// keeps the tokens between app launches
interface TokenStorage {
    fun load(): TokenResponseDto?
    fun save(tokens: TokenResponseDto)
    fun clear()
}
