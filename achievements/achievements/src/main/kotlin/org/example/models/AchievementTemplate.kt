package org.example.models

import org.example.enums.Achievement

abstract class AchievementTemplate {
    abstract val achievement: Achievement
    
    
    abstract suspend fun isAchieved(username: String): Boolean
} 