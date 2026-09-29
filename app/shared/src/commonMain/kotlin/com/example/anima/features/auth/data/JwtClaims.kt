package com.example.anima.features.auth.data

import com.example.anima.features.auth.presentation.register.AccountType
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

// what the client reads from the access token, the server is the one that verifies it
data class JwtClaims(
    val userId: String,
    val accountType: AccountType,
    val expiresAtSeconds: Long,
)

@OptIn(ExperimentalEncodingApi::class)
fun parseJwtClaims(token: String): JwtClaims? = runCatching {
    val payload = token.split(".")[1]
    val text = Base64.UrlSafe.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL).decode(payload).decodeToString()
    val obj = Json.parseToJsonElement(text).jsonObject
    JwtClaims(
        userId = obj.getValue("sub").jsonPrimitive.content,
        accountType = AccountType.valueOf(obj.getValue("accountType").jsonPrimitive.content),
        expiresAtSeconds = obj.getValue("exp").jsonPrimitive.longOrNull ?: 0,
    )
}.getOrNull()
