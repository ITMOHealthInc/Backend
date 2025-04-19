package org.example.service

import org.example.repository.StepsRepository

class StepsService(private val repository: StepsRepository) {

    fun getSteps(username: String): Int {
        return repository.getSteps(username) ?: 0
    }
    
    fun setSteps(username: String, steps: Int) {
        repository.updateSteps(username, steps)
    }
}
