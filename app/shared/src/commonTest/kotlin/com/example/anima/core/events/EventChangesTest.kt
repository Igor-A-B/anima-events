package com.example.anima.core.events

import kotlin.test.Test
import kotlin.test.assertEquals

class EventChangesTest {

    @Test
    fun `every notification raises the version`() {
        val changes = EventChanges()
        assertEquals(0, changes.version.value)

        changes.notifyChanged()
        changes.notifyChanged()

        assertEquals(2, changes.version.value)
    }
}
