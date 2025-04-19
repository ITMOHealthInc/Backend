package org.example.service

import org.example.repository.StepsRepository
import org.example.models.TodayStepsResponse
import org.example.models.MonthStepsResponse

class StepsService(private val repository: StepsRepository) {

    fun getTodaySteps(username: String): TodayStepsResponse {
        return repository.getTodaySteps(username)
    }

    fun setSteps(username: String, steps: Int): TodayStepsResponse {
        return repository.updateSteps(username, steps)
    }

    fun updateGoal(username: String, goal: Int): TodayStepsResponse {
        repository.updateGoal(username, goal)
        return repository.getTodaySteps(username)
    }

    fun getMonthSteps(username: String, month: Int, year: Int): MonthStepsResponse {
        return repository.getMonthSteps(username, month, year)
    }
}
