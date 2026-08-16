package com.example.anima

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform