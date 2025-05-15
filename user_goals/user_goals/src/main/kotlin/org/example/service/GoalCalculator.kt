package org.example.service

import org.example.enums.ActivityLevel
import org.example.enums.GoalType

/**
 * Класс для вычисления целей пользователя на основе типа цели и уровня активности
 */
class GoalCalculator {
    
    companion object {
        /**
         * Вычисляет цель по калориям
         */
        fun calculateCalorieGoal(goalType: GoalType, activityLevel: ActivityLevel): Int {
            return when (goalType) {
                GoalType.WEIGHT_LOSS -> when (activityLevel) {
                    ActivityLevel.LOW -> 1800
                    ActivityLevel.MEDIUM -> 2000
                    ActivityLevel.HIGH -> 2200
                }
                GoalType.WEIGHT_GAIN -> when (activityLevel) {
                    ActivityLevel.LOW -> 2500
                    ActivityLevel.MEDIUM -> 2800
                    ActivityLevel.HIGH -> 3200
                }
                GoalType.WEIGHT_MAINTENANCE -> when (activityLevel) {
                    ActivityLevel.LOW -> 2000
                    ActivityLevel.MEDIUM -> 2400
                    ActivityLevel.HIGH -> 2800
                }
            }
        }
        
        /**
         * Вычисляет цель по воде (в миллилитрах)
         */
        fun calculateWaterGoal(activityLevel: ActivityLevel): Int {
            return when (activityLevel) {
                ActivityLevel.LOW -> 2000    // 2 литра
                ActivityLevel.MEDIUM -> 2500 // 2.5 литра
                ActivityLevel.HIGH -> 3000   // 3 литра
            }
        }
        
        /**
         * Вычисляет цель по шагам
         */
        fun calculateStepsGoal(activityLevel: ActivityLevel): Int {
            return when (activityLevel) {
                ActivityLevel.LOW -> 5000
                ActivityLevel.MEDIUM -> 8000
                ActivityLevel.HIGH -> 12000
            }
        }
        
        /**
         * Вычисляет цель по белкам (в граммах)
         */
        fun calculateProteinsGoal(goalType: GoalType): Int {
            return when (goalType) {
                GoalType.WEIGHT_LOSS -> 120  // Больше белка для похудения
                GoalType.WEIGHT_GAIN -> 150  // Высокое содержание белка для набора мышц
                GoalType.WEIGHT_MAINTENANCE -> 90
            }
        }
        
        /**
         * Вычисляет цель по жирам (в граммах)
         */
        fun calculateFatsGoal(goalType: GoalType): Int {
            return when (goalType) {
                GoalType.WEIGHT_LOSS -> 40
                GoalType.WEIGHT_GAIN -> 80
                GoalType.WEIGHT_MAINTENANCE -> 65
            }
        }
        
        /**
         * Вычисляет цель по углеводам (в граммах)
         */
        fun calculateCarbohydratesGoal(goalType: GoalType): Int {
            return when (goalType) {
                GoalType.WEIGHT_LOSS -> 150
                GoalType.WEIGHT_GAIN -> 350
                GoalType.WEIGHT_MAINTENANCE -> 250
            }
        }
    }
} 