package org.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class WaterMealRequestDTO(
    val waterAmount: Long
) 