package org.example.repository

import org.example.Database
import org.example.models.MealsContent
import org.example.enums.TypesMealsContent
import java.sql.ResultSet

class MealsContentRepository {
    fun insert(mealContent: MealsContent) {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO meals_content (meal_id, content_id, type_content)
                VALUES (?, ?, ?)
            """)
            stmt.setLong(1, mealContent.mealId)
            stmt.setLong(2, mealContent.contentId)
            stmt.setString(3, mealContent.typeContent.name)
            stmt.executeUpdate()
        }
    }

    fun findByMealId(mealId: Long): List<MealsContent> {
        val contents = mutableListOf<MealsContent>()
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                SELECT * FROM meals_content 
                WHERE meal_id = ?
            """)
            stmt.setLong(1, mealId)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                contents.add(rs.toMealContent())
            }
        }
        return contents
    }

    fun deleteByMealId(mealId: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM meals_content WHERE meal_id = ?")
            stmt.setLong(1, mealId)
            return stmt.executeUpdate() > 0
        }
    }

    fun deleteByContentId(contentId: Long, type: TypesMealsContent): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                DELETE FROM meals_content 
                WHERE content_id = ? AND type_content = ?
            """)
            stmt.setLong(1, contentId)
            stmt.setString(2, type.name)
            return stmt.executeUpdate() > 0
        }
    }

    private fun ResultSet.toMealContent(): MealsContent {
        return MealsContent(
            mealId = getLong("history_meal_id"),
            contentId = getLong("content_id"),
            typeContent = TypesMealsContent.valueOf(getString("type_content"))
        )
    }
} 