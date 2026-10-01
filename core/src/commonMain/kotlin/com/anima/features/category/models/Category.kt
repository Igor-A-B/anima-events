package com.anima.features.category.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val description: String,
)
