package com.example.anima.features.visitor.models

import kotlinx.serialization.Serializable

@Serializable
data class Visitor(
    val id: String,
    val userId: String,
)
