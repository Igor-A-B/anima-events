package com.example.anima.core.image

import com.example.anima.core.network.apiBaseUrl as deviceApiBaseUrl

private val loopbackHosts = setOf("127.0.0.1", "localhost")

// the server builds image urls from STORAGE_PUBLIC_BASE_URL (default http://127.0.0.1:9199), which is
// the server's own loopback. When the app reaches the api through another host (android emulator's
// 10.0.2.2, a LAN ip), the image follows it, so it loads without adb reverse.
fun deviceImageUrl(url: String, apiBaseUrl: String = deviceApiBaseUrl): String {
    val (scheme, rest) = url.splitScheme() ?: return url
    val authority = rest.substringBefore('/')
    if (authority.substringBefore(':') !in loopbackHosts) return url
    val apiHost = apiBaseUrl.splitScheme()?.second?.substringBefore('/')?.substringBefore(':') ?: return url
    if (apiHost.isEmpty() || apiHost in loopbackHosts) return url
    val port = authority.substringAfter(':', "")
    val newAuthority = if (port.isEmpty()) apiHost else "$apiHost:$port"
    return "$scheme://$newAuthority${rest.removePrefix(authority)}"
}

private fun String.splitScheme(): Pair<String, String>? {
    val i = indexOf("://")
    return if (i <= 0) null else substring(0, i) to substring(i + 3)
}
