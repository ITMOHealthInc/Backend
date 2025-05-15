package org.example.utils

import java.security.MessageDigest

object PasswordHasher {
    private const val SALT = "secter_123" // Лучше использовать уникальную соль для каждого пользователя

    fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest("$password$SALT".toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun verify(inputPassword: String, hashedPassword: String): Boolean {
        return hash(inputPassword) == hashedPassword
    }
}