package org.example.models

data class RegisterRequest(val username: String, val password: String)
data class LoginRequest(val username: String, val password: String)
data class AuthResponse(val token: String)
data class ErrorResponse(val message: String)
data class ValidateResponse(val username: String, val message: String)
data class User(val username: String, val password: String)