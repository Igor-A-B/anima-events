package com.example.anima.features.address.models

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val exhibitorId: String
)