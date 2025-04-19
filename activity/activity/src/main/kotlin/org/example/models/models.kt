package org.example.models

data class SetStepsRequest(val steps: Int)
data class UpdateGoalRequest(val goal: Int)
data class GetMonthRequest(val month: Int, val year: Int)
data class TodayStepsResponse(val steps: Int, val goal: Int)
data class ErrorResponse(val message: String)

data class MonthStepsResponse(
    val totalSteps: Int,          // Общее количество шагов за месяц
    val averageSteps: Double,     // Среднее количество шагов в день
    val maxSteps: Int,            // Максимальное количество шагов за день
    val days: List<DaySteps>      // Детали по дням
)
data class DaySteps(
    val date: String,             // Дата в формате "YYYY-MM-DD"
    val steps: Int,               // Шаги за этот день
    val goal: Int                 // Цель за этот день
)