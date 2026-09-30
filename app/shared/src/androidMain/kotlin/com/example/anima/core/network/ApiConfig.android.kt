package com.example.anima.core.network

import android.os.Build

// set by the app module at startup, from BuildConfig.API_BASE_URL
object AndroidApiConfig {
    var baseUrl: String = ""
}

private const val EMULATOR_HOST_URL = "http://10.0.2.2:8080"

// the emulator reaches the host machine on 10.0.2.2, a real device uses the configured url
private fun isEmulator() =
    Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.contains("emulator") ||
        Build.HARDWARE in setOf("goldfish", "ranchu") ||
        Build.PRODUCT.contains("sdk_gphone")

actual val apiBaseUrl: String
    get() = if (isEmulator() || AndroidApiConfig.baseUrl.isBlank()) EMULATOR_HOST_URL else AndroidApiConfig.baseUrl
