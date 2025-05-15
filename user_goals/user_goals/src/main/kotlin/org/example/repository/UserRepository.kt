package org.example.repository

import org.example.Database
import java.sql.ResultSet

class UserRepository {
    fun exists(userId: String): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?")
            stmt.setString(1, userId)
            val rs = stmt.executeQuery()
            return rs.next()
        }
    }
} 