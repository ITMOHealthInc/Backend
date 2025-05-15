package org.example.repository

import org.example.Database
import org.example.enums.ActivityLevel
import org.example.enums.GoalType
import org.example.models.UserGoal
import java.sql.ResultSet
import java.sql.Statement

class UserGoalRepository {
    
    private fun resultSetToUserGoal(rs: ResultSet): UserGoal {
        return UserGoal(
            user_id = rs.getString("user_id"),
            goal_type = GoalType.valueOf(rs.getString("goal_type")),
            activity_level = ActivityLevel.valueOf(rs.getString("activity_level")),
            calorie_goal = rs.getInt("calorie_goal"),
            water_goal = rs.getInt("water_goal"),
            steps_goal = rs.getInt("steps_goal"),
            proteins_goal = rs.getInt("proteins_goal"),
            fats_goal = rs.getInt("fats_goal"),
            carbohydrates_goal = rs.getInt("carbohydrates_goal"),
            weight_goal = rs.getDouble("weight_goal")
        )
    }

    fun create(userGoal: UserGoal): UserGoal {
        Database.getConnection().use { connection ->
            val preparedStatement = connection.prepareStatement("""
                INSERT INTO user_goals (
                    user_id, goal_type, activity_level, calorie_goal, 
                    water_goal, steps_goal, proteins_goal, fats_goal, carbohydrates_goal, weight_goal
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """)
            
            preparedStatement.setString(1, userGoal.user_id)
            preparedStatement.setString(2, userGoal.goal_type.name)
            preparedStatement.setString(3, userGoal.activity_level.name)
            preparedStatement.setInt(4, userGoal.calorie_goal)
            preparedStatement.setInt(5, userGoal.water_goal)
            preparedStatement.setInt(6, userGoal.steps_goal)
            preparedStatement.setInt(7, userGoal.proteins_goal)
            preparedStatement.setInt(8, userGoal.fats_goal)
            preparedStatement.setInt(9, userGoal.carbohydrates_goal)
            preparedStatement.setDouble(10, userGoal.weight_goal)
            
            preparedStatement.executeUpdate()
        }
        return userGoal
    }

    fun getByUserId(userId: String): List<UserGoal> {
        val userGoals = mutableListOf<UserGoal>()
        
        Database.getConnection().use { connection ->
            val preparedStatement = connection.prepareStatement("""
                SELECT * FROM user_goals WHERE user_id = ?
            """)
            preparedStatement.setString(1, userId)
            
            val resultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                userGoals.add(resultSetToUserGoal(resultSet))
            }
        }
        
        return userGoals
    }
    
    fun getById(userId: String): UserGoal? {
        Database.getConnection().use { connection ->
            val preparedStatement = connection.prepareStatement("""
                SELECT * FROM user_goals WHERE user_id = ?
            """)
            preparedStatement.setString(1, userId)
            
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSetToUserGoal(resultSet)
            }
        }
        return null
    }

    fun update(userGoal: UserGoal): UserGoal {
        Database.getConnection().use { connection ->
            val preparedStatement = connection.prepareStatement("""
                UPDATE user_goals SET 
                    goal_type = ?,
                    activity_level = ?,
                    calorie_goal = ?,
                    water_goal = ?,
                    steps_goal = ?,
                    proteins_goal = ?,
                    fats_goal = ?,
                    carbohydrates_goal = ?,
                    weight_goal = ?
                WHERE user_id = ?
            """)
            
            preparedStatement.setString(1, userGoal.goal_type.name)
            preparedStatement.setString(2, userGoal.activity_level.name)
            preparedStatement.setInt(3, userGoal.calorie_goal)
            preparedStatement.setInt(4, userGoal.water_goal)
            preparedStatement.setInt(5, userGoal.steps_goal)
            preparedStatement.setInt(6, userGoal.proteins_goal)
            preparedStatement.setInt(7, userGoal.fats_goal)
            preparedStatement.setInt(8, userGoal.carbohydrates_goal)
            preparedStatement.setDouble(9, userGoal.weight_goal)
            preparedStatement.setString(10, userGoal.user_id)
            
            preparedStatement.executeUpdate()
        }
        return userGoal
    }

    fun delete(userId: String): Boolean {
        Database.getConnection().use { connection ->
            val preparedStatement = connection.prepareStatement("""
                DELETE FROM user_goals WHERE user_id = ?
            """)
            preparedStatement.setString(1, userId)
            
            return preparedStatement.executeUpdate() > 0
        }
    }

    fun getAll(): List<UserGoal> {
        val userGoals = mutableListOf<UserGoal>()
        
        Database.getConnection().use { connection ->
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM user_goals")
            
            while (resultSet.next()) {
                userGoals.add(resultSetToUserGoal(resultSet))
            }
        }
        
        return userGoals
    }
} 