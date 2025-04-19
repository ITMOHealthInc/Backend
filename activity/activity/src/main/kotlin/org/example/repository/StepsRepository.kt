package org.example.repository

import java.time.LocalDate
import org.example.Database
import org.example.models.*
import java.sql.Connection
import java.sql.Date

class StepsRepository {
    private fun getTodayDate(): Date {
        return Date.valueOf(LocalDate.now())
    }

    private fun createEntity(username: String, steps: Int): Pair<Int, Int> {
        val todayDate = getTodayDate()
        var goal = 10000

        Database.getConnection().use { connection ->
            connection.prepareStatement(
                """
                    SELECT goal FROM user_steps WHERE username = ? ORDER BY date DESC LIMIT 1
                """
            ).use { statement ->
                statement.setString(1, username)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        goal = resultSet.getInt("goal")
                    }
                }
            }

            connection.prepareStatement(
                """
                INSERT INTO user_steps (username, steps, goal, date) 
                VALUES (?, ?, ?, ?)
            """
            ).use { insertStmt ->
                insertStmt.setString(1, username)
                insertStmt.setInt(2, steps)
                insertStmt.setInt(3, goal)
                insertStmt.setDate(4, todayDate)
                insertStmt.executeUpdate()
            }
        }

        return Pair(steps, goal)
    }


    fun getTodaySteps(username: String): TodayStepsResponse {
        val today = getTodayDate()

        Database.getConnection().use { connection ->
            connection.prepareStatement("SELECT steps, goal FROM user_steps WHERE username = ? AND date = ?").use { statement ->
                statement.setString(1, username)
                statement.setDate(2, today)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        val steps = resultSet.getInt("steps")
                        val goal = resultSet.getInt("goal")
                        return TodayStepsResponse(steps, goal)
                    }
                }
            }
        }

        val (steps, goal) = createEntity(username, 0)
        return TodayStepsResponse(steps, goal)
    }

    fun updateSteps(username: String, steps: Int): TodayStepsResponse {
        val today = getTodayDate()

        Database.getConnection().use { connection ->
            connection.prepareStatement("UPDATE user_steps SET steps = ? WHERE username = ? AND date = ?").use { statement ->
                statement.setInt(1, steps)
                statement.setString(2, username)
                statement.setDate(3, today)
                val updated = statement.executeUpdate()
                if (updated == 0) {
                    val (steps, goal) = createEntity(username, steps)
                    return TodayStepsResponse(steps, goal)
                }
            }
        }
        return getTodaySteps(username)
    }

    fun updateGoal(username: String, goal: Int) {
        val today = getTodayDate()

        Database.getConnection().use { connection ->
            val updatedRows = connection.prepareStatement("UPDATE user_steps SET goal = ? WHERE username = ? AND date = ?").use { statement ->
                statement.setInt(1, goal)
                statement.setString(2, username)
                statement.setDate(3, today)
                statement.executeUpdate()
            }

            if (updatedRows == 0) {
                connection.prepareStatement("""
                INSERT INTO user_steps (username, steps, goal, date) 
                VALUES (?, 0, ?, ?)
            """).use { insertStmt ->
                    insertStmt.setString(1, username)
                    insertStmt.setInt(2, goal)
                    insertStmt.setDate(3, today)
                    insertStmt.executeUpdate()
                }
            }
        }
    }


    fun getMonthSteps(username: String, month: Int, year: Int): MonthStepsResponse {
        val days = mutableListOf<DaySteps>()
        Database.getConnection().use { connection ->
            connection.prepareStatement("SELECT date, steps, goal FROM user_steps WHERE username = ? AND EXTRACT(MONTH FROM date) = ? AND EXTRACT(YEAR FROM date) = ?").use { statement ->
                statement.setString(1, username)
                statement.setInt(2, month)
                statement.setInt(3, year)
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        days.add(DaySteps(
                            date = resultSet.getString("date"),
                            steps = resultSet.getInt("steps"),
                            goal = resultSet.getInt("goal")
                        ))
                    }
                }
            }
        }

        if (days.isEmpty()) {
            return MonthStepsResponse(0, 0.0, 0, emptyList())
        }

        val totalSteps = days.sumOf { it.steps }
        val averageSteps = totalSteps / days.size.toDouble()
        val maxSteps = days.maxOfOrNull { it.steps } ?: 0

        return MonthStepsResponse(totalSteps, averageSteps, maxSteps, days)
    }
}
