package org.example.service

import org.example.enums.Achievement
import org.example.models.AchievementTemplate
import org.example.models.FirstMealAchievement
import org.example.repository.UserRepository

class AchievementManager(
    private val userRepository: UserRepository = UserRepository()
) {
    private val achievements: Map<Achievement, AchievementTemplate> = mapOf(
        Achievement.FIRST_FOOD_ENTRY to FirstMealAchievement()
    )

    
    suspend fun isAchievementUnlocked(username: String, achievement: Achievement): Boolean {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val achievementTemplate = achievements[achievement] 
            ?: throw IllegalArgumentException("Achievement $achievement not found")
        return achievementTemplate.isAchieved(username)
    }

    
    suspend fun getAllAchievementsStatus(username: String): Map<Achievement, Boolean> {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        return achievements.mapValues { (_, template) -> template.isAchieved(username) }
    }
} 