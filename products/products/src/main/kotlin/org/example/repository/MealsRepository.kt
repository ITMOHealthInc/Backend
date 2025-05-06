package org.example.repository

import org.example.Database
import org.example.models.Meals
import org.example.enums.TypesMeals
import java.sql.ResultSet
import java.time.LocalDateTime

class MealsRepository {
    fun insert(meal: Meals): Meals {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO meals (type, added_at, username)
                VALUES (?, ?, ?)
                RETURNING *
            """)
            stmt.setString(1, meal.type.name)
            stmt.setObject(2, meal.addedAt)
            stmt.setString(3, meal.username)
            
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.toMeal() else throw IllegalStateException("Failed to create meal")
        }
    }

    fun findById(id: Long): Meals? {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM meals WHERE id = ?")
            stmt.setLong(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.toMeal() else null
        }
    }

    fun findByUsername(username: String): List<Meals> {
        val meals = mutableListOf<Meals>()
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM meals WHERE username = ? ORDER BY added_at DESC")
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                meals.add(rs.toMeal())
            }
        }
        return meals
    }

    fun findAll(): List<Meals> {
        val meals = mutableListOf<Meals>()
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM meals ORDER BY added_at DESC")
            val rs = stmt.executeQuery()
            while (rs.next()) {
                meals.add(rs.toMeal())
            }
        }
        return meals
    }

    fun deleteById(id: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM meals WHERE id = ?")
            stmt.setLong(1, id)
            return stmt.executeUpdate() > 0
        }
    }

    fun update(meal: Meals): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                UPDATE meals 
                SET type = ?, added_at = ?, username = ?
                WHERE id = ?
            """)
            stmt.setString(1, meal.type.name)
            stmt.setObject(2, meal.addedAt)
            stmt.setString(3, meal.username)
            stmt.setLong(4, meal.id)
            return stmt.executeUpdate() > 0
        }
    }

    private fun ResultSet.toMeal(): Meals {
        return Meals(
            id = getLong("id"),
            type = TypesMeals.valueOf(getString("type")),
            addedAt = getTimestamp("added_at").toLocalDateTime(),
            username = getString("username")
        )
    }
} 