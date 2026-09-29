package com.anima.utils

import java.security.MessageDigest
import kotlin.io.encoding.Base64

class TokenHashUtil {
    companion object {
        fun sha256(value: String): String {
            val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
            return Base64.encode(digest)
        }
    }
}