package org.example.models

data class RegisterRequest(
    val username: String,       
    val password: String,      
    val name: String,          
    val profilePictureBase64: String? = null  
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String
)

data class ErrorResponse(
    val message: String
)

data class ValidateResponse(
    val username: String,
    val message: String = "Token is valid"
)

data class User(
    val username: String,
    val password: String,
    val name: String,
    val profilePicturePath: String? = null  
)

data class UpdateProfileRequest(
    val name: String? = null,
    val profilePictureBase64: String? = null
)