package org.example.repository

import org.example.Database
import org.example.models.RecipeProduct

class RecipeProductRepository {
    fun insert(recipeProduct: RecipeProduct) {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO recipe_products (recipe_id, product_id)
                VALUES (?, ?)
            """)
            stmt.setLong(1, recipeProduct.recipeId)
            stmt.setLong(2, recipeProduct.productId)
            stmt.executeUpdate()
        }
    }

    fun findByRecipeId(recipeId: Long): List<RecipeProduct> {
        val recipeProducts = mutableListOf<RecipeProduct>()
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                SELECT * FROM recipe_products 
                WHERE recipe_id = ?
            """)
            stmt.setLong(1, recipeId)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                recipeProducts.add(RecipeProduct(
                    recipeId = rs.getLong("recipe_id"),
                    productId = rs.getLong("product_id")
                ))
            }
        }
        return recipeProducts
    }

    fun deleteByRecipeId(recipeId: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM recipe_products WHERE recipe_id = ?")
            stmt.setLong(1, recipeId)
            return stmt.executeUpdate() > 0
        }
    }

    fun deleteByProductId(productId: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM recipe_products WHERE product_id = ?")
            stmt.setLong(1, productId)
            return stmt.executeUpdate() > 0
        }
    }
} 