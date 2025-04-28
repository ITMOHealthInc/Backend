package org.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeRequestDTO(
    val name: String,
    val productIds: List<Long>
) 