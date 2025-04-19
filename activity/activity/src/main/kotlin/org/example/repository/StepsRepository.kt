package org.example.repository

import org.example.Database
import java.sql.Connection

class StepsRepository {

    fun getSteps(username: String): Int? {
        Database.getConnection().use { connection ->
            connection.prepareStatement("SELECT steps FROM user_steps WHERE username = ?").use { statement ->
                statement.setString(1, username)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        return resultSet.getInt("steps")
                    }
                }
            }
        }
        return null
    }

    fun updateSteps(username: String, steps: Int) {
        Database.getConnection().use { connection ->
            connection.prepareStatement("UPDATE user_steps SET steps = ? WHERE username = ?").use { statement ->
                statement.setInt(1, steps)
                statement.setString(2, username)
                val updated = statement.executeUpdate()
                if (updated == 0) {
                    connection.prepareStatement("INSERT INTO user_steps (username, steps) VALUES (?, ?)").use { insertStmt ->
                        insertStmt.setString(1, username)
                        insertStmt.setInt(2, steps)
                        insertStmt.executeUpdate()
                    }
                }
            }
        }
    }
}
