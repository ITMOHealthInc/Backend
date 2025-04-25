package org.example.repository

import org.example.Database
import org.example.models.UserProduct

class UserProductRepository {
    fun insert(userProduct: UserProduct) {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO user_products (username, product_id)
                VALUES (?, ?)
            """)
            stmt.setString(1, userProduct.username)
            stmt.setLong(2, userProduct.productId)
            stmt.executeUpdate()
        }
    }

    fun findByUsername(username: String): List<Long> {
        val productIds = mutableListOf<Long>()
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT product_id FROM user_products WHERE username = ?")
            stmt.setString(1, username)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                productIds.add(rs.getLong("product_id"))
            }
        }
        return productIds
    }

    fun findByProductId(productId: Long): String? {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT username FROM user_products WHERE product_id = ?")
            stmt.setLong(1, productId)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.getString("username") else null
        }
    }

    fun deleteByProductId(productId: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM user_products WHERE product_id = ?")
            stmt.setLong(1, productId)
            return stmt.executeUpdate() > 0
        }
    }

    fun deleteByUsername(username: String): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM user_products WHERE username = ?")
            stmt.setString(1, username)
            return stmt.executeUpdate() > 0
        }
    }
}