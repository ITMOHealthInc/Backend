package org.example.service

import org.example.dto.UserGoalDto
import org.example.dto.UserGoalRequestDto
import org.example.mappers.UserGoalMapper
import org.example.models.UserGoal
import org.example.repository.UserGoalRepository
import org.example.repository.UserRepository
import java.lang.RuntimeException

class GoalNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedAccessException(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : RuntimeException(message)
class GoalAlreadyExistsException(message: String) : RuntimeException(message)

class UserGoalService(
    private val userGoalRepository: UserGoalRepository = UserGoalRepository(),
    private val userRepository: UserRepository = UserRepository()
) {
    
    
    
    
    
    
    private fun validateUser(userId: String) {
        if (!userRepository.exists(userId)) {
            throw UserNotFoundException("User with ID $userId not found")
        }
    }
    
    fun createUserGoal(requestDto: UserGoalRequestDto, userId: String): UserGoalDto {
        validateUser(userId)
        
        
        val existingGoals = userGoalRepository.getByUserId(userId)
        if (existingGoals.isNotEmpty()) {
            throw GoalAlreadyExistsException("User already has a goal")
        }
        
        
        val userGoal = UserGoal(
            user_id = userId,
            goal_type = requestDto.goal_type,
            activity_level = requestDto.activity_level,
            
            calorie_goal = GoalCalculator.calculateCalorieGoal(
                requestDto.goal_type, 
                requestDto.activity_level, 
                requestDto.weight_goal
            ),
            water_goal = GoalCalculator.calculateWaterGoal(
                requestDto.activity_level, 
                requestDto.weight_goal
            ),
            steps_goal = GoalCalculator.calculateStepsGoal(
                requestDto.activity_level, 
                requestDto.weight_goal
            ),
            proteins_goal = GoalCalculator.calculateProteinsGoal(
                requestDto.goal_type, 
                requestDto.weight_goal
            ),
            fats_goal = GoalCalculator.calculateFatsGoal(
                requestDto.goal_type, 
                requestDto.weight_goal
            ),
            carbohydrates_goal = GoalCalculator.calculateCarbohydratesGoal(
                requestDto.goal_type, 
                requestDto.weight_goal
            ),
            weight_goal = requestDto.weight_goal
        )
        
        val createdUserGoal = userGoalRepository.create(userGoal)
        return UserGoalMapper.toDto(createdUserGoal)
    }
    
    fun getUserGoal(userId: String): UserGoalDto {
        validateUser(userId)
        
        val userGoal = userGoalRepository.getById(userId)
            ?: throw GoalNotFoundException("Goal not found for user $userId")
        
        return UserGoalMapper.toDto(userGoal)
    }
    
    fun getUserGoalByUserId(userId: String): UserGoalDto {
        validateUser(userId)
        
        val userGoals = userGoalRepository.getByUserId(userId)
        if (userGoals.isEmpty()) {
            throw GoalNotFoundException("Goal not found for user $userId")
        }
        
        
        return UserGoalMapper.toDto(userGoals.first())
    }
    
    fun updateUserGoal(requestDto: UserGoalRequestDto, userId: String): UserGoalDto {
        validateUser(userId)
        
        val existingUserGoal = userGoalRepository.getById(userId)
            ?: throw GoalNotFoundException("Goal not found for user $userId")
        
        
        val updatedUserGoal = existingUserGoal.copy(
            goal_type = requestDto.goal_type,
            activity_level = requestDto.activity_level,
            calorie_goal = GoalCalculator.calculateCalorieGoal(
                requestDto.goal_type, 
                requestDto.activity_level, 
                requestDto.weight_goal
            ),
            water_goal = GoalCalculator.calculateWaterGoal(
                requestDto.activity_level, 
                requestDto.weight_goal
            ),
            steps_goal = GoalCalculator.calculateStepsGoal(
                requestDto.activity_level, 
                requestDto.weight_goal
            ),
            proteins_goal = GoalCalculator.calculateProteinsGoal(
                requestDto.goal_type, 
                requestDto.weight_goal
            ),
            fats_goal = GoalCalculator.calculateFatsGoal(
                requestDto.goal_type, 
                requestDto.weight_goal
            ),
            carbohydrates_goal = GoalCalculator.calculateCarbohydratesGoal(
                requestDto.goal_type, 
                requestDto.weight_goal
            ),
            weight_goal = requestDto.weight_goal
        )
        
        userGoalRepository.update(updatedUserGoal)
        
        return UserGoalMapper.toDto(updatedUserGoal)
    }
    
    fun deleteUserGoal(userId: String) {
        validateUser(userId)
        
        val existingUserGoal = userGoalRepository.getById(userId)
            ?: throw GoalNotFoundException("Goal not found for user $userId")
        
        val deleted = userGoalRepository.delete(userId)
        if (!deleted) {
            throw RuntimeException("Failed to delete goal for user $userId")
        }
    }
    
    
    fun getAllUserGoals(): List<UserGoalDto> {
        val userGoals = userGoalRepository.getAll()
        return userGoals.map { UserGoalMapper.toDto(it) }
    }
} 