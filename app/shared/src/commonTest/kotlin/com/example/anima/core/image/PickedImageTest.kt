package com.example.anima.core.image

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PickedImageTest {
    private fun bytes(vararg v: Int) = ByteArray(v.size) { v[it].toByte() }

    @Test
    fun sniffsJpeg() {
        assertEquals("image/jpeg", PickedImage.sniff(bytes(0xFF, 0xD8, 0xFF, 0xE0, 0))?.second)
    }

    @Test
    fun sniffsPng() {
        assertEquals("image/png", PickedImage.sniff(bytes(0x89, 0x50, 0x4E, 0x47, 0x0D))?.second)
    }

    @Test
    fun sniffsWebp() {
        val webp = "RIFF".encodeToByteArray() + bytes(1, 2, 3, 4) + "WEBP".encodeToByteArray()
        assertEquals("image/webp", PickedImage.sniff(webp)?.second)
    }

    @Test
    fun rejectsHeicGifAndShortInput() {
        val heic = bytes(0, 0, 0, 0x18) + "ftypheic".encodeToByteArray()
        assertNull(PickedImage.sniff(heic))
        assertNull(PickedImage.sniff("GIF89a".encodeToByteArray()))
        assertNull(PickedImage.sniff(bytes(0xFF)))
        assertNull(PickedImage.sniff(ByteArray(0)))
    }

    @Test
    fun mimeByExtension() {
        assertEquals("image/png", PickedImage.mimeTypeOf("a.PNG"))
        assertNull(PickedImage.mimeTypeOf("a.heic"))
        assertNull(PickedImage.mimeTypeOf("noext"))
    }
}
