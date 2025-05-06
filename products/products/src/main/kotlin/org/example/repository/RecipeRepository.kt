package org.example.repository

import org.example.Database
import org.example.models.Recipe
import org.example.models.RecipeProduct
import java.sql.ResultSet

class RecipeRepository {
    fun insert(recipe: Recipe): Long {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO recipes (name, username)
                VALUES (?, ?)
                RETURNING id
            """)
            stmt.setString(1, recipe.name)
            stmt.setString(2, recipe.username)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.getLong("id") else throw IllegalStateException("Failed to insert recipe")
        }
    }

    fun findById(id: Long): Recipe? {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM recipes WHERE id = ?")
            stmt.setLong(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.toRecipe() else null
        }
    }

    fun findByUsername(username: String): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM recipes WHERE username = ?")
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                recipes.add(rs.toRecipe())
            }
        }
        return recipes
    }

    fun deleteById(id: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM recipes WHERE id = ?")
            stmt.setLong(1, id)
            return stmt.executeUpdate() > 0
        }
    }

    fun update(recipe: Recipe): Boolean {
        require(recipe.id != null) { "Recipe ID must not be null for update" }
        
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                UPDATE recipes SET
                    name = ?,
                    username = ?
                WHERE id = ?
            """)
            stmt.setString(1, recipe.name)
            stmt.setString(2, recipe.username)
            stmt.setLong(3, recipe.id!!)
            return stmt.executeUpdate() > 0
        }
    }

    private fun ResultSet.toRecipe(): Recipe {
        return Recipe(
            id = getLong("id"),
            name = getString("name"),
            username = getString("username")
        )
    }
} 