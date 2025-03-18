package org.example

import org.example.models.User
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

object UserRepository {

    fun createUser(username: String, password: String): Boolean {
        val sql = "INSERT INTO users (username, password) VALUES (?, ?)"
        Database.getConnection().use { connection ->
            connection.prepareStatement(sql).use { statement: PreparedStatement ->
                statement.setString(1, username)
                statement.setString(2, password)
                return statement.executeUpdate() > 0
            }
        }
    }

    fun findUserByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"
        Database.getConnection().use { connection ->
            connection.prepareStatement(sql).use { statement: PreparedStatement ->
                statement.setString(1, username)
                val resultSet: ResultSet = statement.executeQuery()
                return if (resultSet.next()) {
                    User(resultSet.getString("username"), resultSet.getString("password"))
                } else {
                    null
                }
            }
        }
    }
}