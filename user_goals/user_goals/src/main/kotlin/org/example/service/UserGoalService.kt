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
    
    // init {
    //     // Ensure the table exists
    //     userGoalRepository.createTable()
    // }
    
    private fun validateUser(userId: String) {
        if (!userRepository.exists(userId)) {
            throw UserNotFoundException("User with ID $userId not found")
        }
    }
    
    fun createUserGoal(requestDto: UserGoalRequestDto, userId: String): UserGoalDto {
        validateUser(userId)
        
        // Проверяем, что у пользователя нет уже существующей цели
        val existingGoals = userGoalRepository.getByUserId(userId)
        if (existingGoals.isNotEmpty()) {
            throw GoalAlreadyExistsException("User already has a goal")
        }
        
        // Create a default UserGoal with standard goals based on the request
        val userGoal = UserGoal(
            user_id = userId,
            goal_type = requestDto.goal_type,
            activity_level = requestDto.activity_level,
            // Используем GoalCalculator для расчета целей
            calorie_goal = GoalCalculator.calculateCalorieGoal(requestDto.goal_type, requestDto.activity_level),
            water_goal = GoalCalculator.calculateWaterGoal(requestDto.activity_level),
            steps_goal = GoalCalculator.calculateStepsGoal(requestDto.activity_level),
            proteins_goal = GoalCalculator.calculateProteinsGoal(requestDto.goal_type),
            fats_goal = GoalCalculator.calculateFatsGoal(requestDto.goal_type),
            carbohydrates_goal = GoalCalculator.calculateCarbohydratesGoal(requestDto.goal_type)
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
        
        // Поскольку у пользователя может быть только одна цель, возвращаем первую (и единственную)
        return UserGoalMapper.toDto(userGoals.first())
    }
    
    fun updateUserGoal(requestDto: UserGoalRequestDto, userId: String): UserGoalDto {
        validateUser(userId)
        
        val existingUserGoal = userGoalRepository.getById(userId)
            ?: throw GoalNotFoundException("Goal not found for user $userId")
        
        // Update the existing goal with new values calculated from the request
        val updatedUserGoal = existingUserGoal.copy(
            goal_type = requestDto.goal_type,
            activity_level = requestDto.activity_level,
            calorie_goal = GoalCalculator.calculateCalorieGoal(requestDto.goal_type, requestDto.activity_level),
            water_goal = GoalCalculator.calculateWaterGoal(requestDto.activity_level),
            steps_goal = GoalCalculator.calculateStepsGoal(requestDto.activity_level),
            proteins_goal = GoalCalculator.calculateProteinsGoal(requestDto.goal_type),
            fats_goal = GoalCalculator.calculateFatsGoal(requestDto.goal_type),
            carbohydrates_goal = GoalCalculator.calculateCarbohydratesGoal(requestDto.goal_type)
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
    
    // Admin function - should be secured appropriately
    fun getAllUserGoals(): List<UserGoalDto> {
        val userGoals = userGoalRepository.getAll()
        return userGoals.map { UserGoalMapper.toDto(it) }
    }
} 