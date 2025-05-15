package org.example.service

import org.example.enums.ActivityLevel
import org.example.enums.GoalType


class GoalCalculator {
    companion object {
        fun calculateCalorieGoal(goalType: GoalType, activityLevel: ActivityLevel, weightGoal: Double): Int {
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

            val weightAdjustment = ((weightGoal - 80) * 10).toInt()
            return baseCalories + weightAdjustment
        }
        
        
        fun calculateWaterGoal(activityLevel: ActivityLevel, weightGoal: Double): Int {
            val baseWater = when (activityLevel) {
                ActivityLevel.LOW -> 2000    
                ActivityLevel.MEDIUM -> 2500 
                ActivityLevel.HIGH -> 3000   
            }

            val weightAdjustment = (weightGoal * 30).toInt()
            return baseWater + weightAdjustment
        }
        
        
        fun calculateStepsGoal(activityLevel: ActivityLevel, weightGoal: Double): Int {
            val baseSteps = when (activityLevel) {
                ActivityLevel.LOW -> 5000
                ActivityLevel.MEDIUM -> 8000
                ActivityLevel.HIGH -> 12000
            }
            
            val weightAdjustment = if (weightGoal > 90) -1000 else if (weightGoal < 60) 1000 else 0
            return baseSteps + weightAdjustment
        }
        
        
        fun calculateProteinsGoal(goalType: GoalType, weightGoal: Double): Int {
            val proteinPerKg = when (goalType) {
                GoalType.WEIGHT_LOSS -> 1.8  
                GoalType.WEIGHT_GAIN -> 2.0  
                GoalType.WEIGHT_MAINTENANCE -> 1.2  
            }
            return (weightGoal * proteinPerKg).toInt()
        }
        
        
        fun calculateFatsGoal(goalType: GoalType, weightGoal: Double): Int {
            val fatPerKg = when (goalType) {
                GoalType.WEIGHT_LOSS -> 0.5     
                GoalType.WEIGHT_GAIN -> 1.0     
                GoalType.WEIGHT_MAINTENANCE -> 0.8  
            }
            return (weightGoal * fatPerKg).toInt()
        }
        
        
        fun calculateCarbohydratesGoal(goalType: GoalType, weightGoal: Double): Int {
            val carbsPerKg = when (goalType) {
                GoalType.WEIGHT_LOSS -> 2.0     
                GoalType.WEIGHT_GAIN -> 4.5     
                GoalType.WEIGHT_MAINTENANCE -> 3.0  
            }
            return (weightGoal * carbsPerKg).toInt()
        }
    }
} 