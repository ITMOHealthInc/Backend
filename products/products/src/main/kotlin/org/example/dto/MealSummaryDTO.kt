package org.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class MealSummaryDTO(
    val totalWater: Double,
    val totalKbzhu: Kbzhu
)

@Serializable
data class DailySummaryDTO(
    val date: String,
    val totalWater: Double,
    val totalKbzhu: Kbzhu
) 