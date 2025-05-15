package org.example.dto

import kotlinx.serialization.Serializable
import org.example.enums.ActivityLevel
import org.example.enums.GoalType

@Serializable
data class UserGoalDto(
    val user_id: String? = null,
    val goal_type: GoalType,
    val activity_level: ActivityLevel,
    val calorie_goal: Int,
    val water_goal: Int,
    val steps_goal: Int,
    val proteins_goal: Int,
    val fats_goal: Int,
    val carbohydrates_goal: Int,
    val weight_goal: Double
) 