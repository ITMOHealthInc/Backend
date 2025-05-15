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

@Serializable
data class MonthlySummaryDTO(
    val year: Int,
    val month: Int,
    val totalWater: Double,
    val totalKbzhu: Kbzhu,
    val dailySummaries: List<DailySummaryDTO>
) 