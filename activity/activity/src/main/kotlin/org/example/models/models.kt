package org.example.models

data class SetStepsRequest(val steps: Int)
data class UpdateGoalRequest(val goal: Int)
data class GetMonthRequest(val month: Int, val year: Int)
data class TodayStepsResponse(val steps: Int, val goal: Int)
data class ErrorResponse(val message: String)

data class MonthStepsResponse(
    val totalSteps: Int,          
    val averageSteps: Double,     
    val maxSteps: Int,            
    val days: List<DaySteps>      
)
data class DaySteps(
    val date: String,             
    val steps: Int,               
    val goal: Int                 
)