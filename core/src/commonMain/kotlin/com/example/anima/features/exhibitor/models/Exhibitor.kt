package com.example.anima.features.exhibitor.models

import kotlinx.serialization.Serializable

@Serializable
data class Exhibitor(
    val id: String,
    val userId: String,
    val adresseIds: List<String>,
)