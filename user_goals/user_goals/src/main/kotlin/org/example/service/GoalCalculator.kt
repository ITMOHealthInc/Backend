package org.example.service

import org.example.enums.ActivityLevel
import org.example.enums.GoalType

/**
 * Класс для вычисления целей пользователя на основе типа цели, уровня активности и целевого веса
 */
class GoalCalculator {
    
    companion object {
        /**
         * Вычисляет цель по калориям с учетом целевого веса
         */
        fun calculateCalorieGoal(goalType: GoalType, activityLevel: ActivityLevel, weightGoal: Double): Int {
            // Базовый расчет на основе типа цели и уровня активности
            val baseCalories = when (goalType) {
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
            
            // Дополнительная корректировка на основе целевого веса:
            // Для целевого веса больше 80 кг добавляем калории, для меньшего - уменьшаем
            val weightAdjustment = ((weightGoal - 80) * 10).toInt()
            
            return baseCalories + weightAdjustment
        }
        
        /**
         * Вычисляет цель по воде (в миллилитрах) с учетом целевого веса
         */
        fun calculateWaterGoal(activityLevel: ActivityLevel, weightGoal: Double): Int {
            // Базовое количество воды на основе активности
            val baseWater = when (activityLevel) {
                ActivityLevel.LOW -> 2000    // 2 литра
                ActivityLevel.MEDIUM -> 2500 // 2.5 литра
                ActivityLevel.HIGH -> 3000   // 3 литра
            }
            
            // Корректировка на основе веса: 30мл на каждый кг веса
            val weightAdjustment = (weightGoal * 30).toInt()
            
            return baseWater + weightAdjustment
        }
        
        /**
         * Вычисляет цель по шагам с учетом целевого веса
         */
        fun calculateStepsGoal(activityLevel: ActivityLevel, weightGoal: Double): Int {
            // Базовое количество шагов на основе активности
            val baseSteps = when (activityLevel) {
                ActivityLevel.LOW -> 5000
                ActivityLevel.MEDIUM -> 8000
                ActivityLevel.HIGH -> 12000
            }
            
            // Корректировка на основе веса: для более высокого веса слегка уменьшаем цель
            val weightAdjustment = if (weightGoal > 90) -1000 else if (weightGoal < 60) 1000 else 0
            
            return baseSteps + weightAdjustment
        }
        
        /**
         * Вычисляет цель по белкам (в граммах) с учетом целевого веса
         */
        fun calculateProteinsGoal(goalType: GoalType, weightGoal: Double): Int {
            // Расчет белка на основе целевого веса: рекомендуемые нормы белка на кг веса
            val proteinPerKg = when (goalType) {
                GoalType.WEIGHT_LOSS -> 1.8  // Выше норма для сохранения мышц
                GoalType.WEIGHT_GAIN -> 2.0  // Еще выше для набора мышечной массы
                GoalType.WEIGHT_MAINTENANCE -> 1.2  // Стандартная норма
            }
            
            return (weightGoal * proteinPerKg).toInt()
        }
        
        /**
         * Вычисляет цель по жирам (в граммах) с учетом целевого веса
         */
        fun calculateFatsGoal(goalType: GoalType, weightGoal: Double): Int {
            // Расчет жиров на основе целевого веса
            val fatPerKg = when (goalType) {
                GoalType.WEIGHT_LOSS -> 0.5     // Ниже норма для снижения веса
                GoalType.WEIGHT_GAIN -> 1.0     // Выше для набора
                GoalType.WEIGHT_MAINTENANCE -> 0.8  // Стандартная норма
            }
            
            return (weightGoal * fatPerKg).toInt()
        }
        
        /**
         * Вычисляет цель по углеводам (в граммах) с учетом целевого веса
         */
        fun calculateCarbohydratesGoal(goalType: GoalType, weightGoal: Double): Int {
            // Расчет углеводов на основе целевого веса
            val carbsPerKg = when (goalType) {
                GoalType.WEIGHT_LOSS -> 2.0     // Ниже норма для снижения веса
                GoalType.WEIGHT_GAIN -> 4.5     // Выше для набора
                GoalType.WEIGHT_MAINTENANCE -> 3.0  // Стандартная норма
            }
            
            return (weightGoal * carbsPerKg).toInt()
        }
    }
} 