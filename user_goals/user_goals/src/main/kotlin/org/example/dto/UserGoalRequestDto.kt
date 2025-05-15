package org.example.dto

import kotlinx.serialization.Serializable
import org.example.enums.ActivityLevel
import org.example.enums.GoalType

@Serializable
data class UserGoalRequestDto(
    val goal_type: GoalType,
    val activity_level: ActivityLevel
) 