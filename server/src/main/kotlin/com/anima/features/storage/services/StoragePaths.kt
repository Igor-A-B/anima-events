package com.anima.features.storage.services

import java.util.UUID

// one folder per owner; file names are random so a new upload never overwrites a cached url
object StoragePaths {
    fun eventImage(eventId: UUID, extension: String) = "events/$eventId/${UUID.randomUUID()}.$extension"

    // TODO: used by the profile photo endpoint, delete the previous avatar when replacing it
    fun userAvatar(userId: UUID, extension: String) = "users/$userId/avatar-${UUID.randomUUID()}.$extension"
}
