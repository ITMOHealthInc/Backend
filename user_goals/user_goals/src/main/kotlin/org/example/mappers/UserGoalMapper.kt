package org.example.mappers

import org.example.dto.UserGoalDto
import org.example.models.UserGoal
import java.util.UUID

object UserGoalMapper {
    fun toDto(entity: UserGoal): UserGoalDto {
        return UserGoalDto(
            user_id = entity.user_id,
            goal_type = entity.goal_type,
            activity_level = entity.activity_level,
            calorie_goal = entity.calorie_goal,
            water_goal = entity.water_goal,
            steps_goal = entity.steps_goal,
            proteins_goal = entity.proteins_goal,
            fats_goal = entity.fats_goal,
            carbohydrates_goal = entity.carbohydrates_goal
        )
    }

    fun toEntity(dto: UserGoalDto, userId: String): UserGoal {
        return UserGoal(
            user_id = userId,
            goal_type = dto.goal_type,
            activity_level = dto.activity_level,
            calorie_goal = dto.calorie_goal,
            water_goal = dto.water_goal,
            steps_goal = dto.steps_goal,
            proteins_goal = dto.proteins_goal,
            fats_goal = dto.fats_goal,
            carbohydrates_goal = dto.carbohydrates_goal
        )
    }

    fun updateEntity(entity: UserGoal, dto: UserGoalDto): UserGoal {
        return entity.copy(
            goal_type = dto.goal_type,
            activity_level = dto.activity_level,
            calorie_goal = dto.calorie_goal,
            water_goal = dto.water_goal,
            steps_goal = dto.steps_goal,
            proteins_goal = dto.proteins_goal,
            fats_goal = dto.fats_goal,
            carbohydrates_goal = dto.carbohydrates_goal
        )
    }
} 