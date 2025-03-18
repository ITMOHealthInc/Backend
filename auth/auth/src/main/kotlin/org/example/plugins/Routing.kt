package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import org.example.UserRepository
import org.example.models.*

fun Application.configureRouting() {
    val secret = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET not found in environment variables")
    val issuer = System.getenv("JWT_ISSUER") ?: throw IllegalStateException("JWT_ISSUER not found in environment variables")
    val audience = System.getenv("JWT_AUDIENCE") ?: throw IllegalStateException("JWT_AUDIENCE not found in environment variables")
    val algorithm = Algorithm.HMAC256(secret)

    routing {
        post("/register") {
            val registerRequest = call.receive<RegisterRequest>()
            val userExists = UserRepository.findUserByUsername(registerRequest.username) != null
            if (userExists) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Username already exists"))
            } else {
                if (UserRepository.createUser(registerRequest.username, registerRequest.password)) {
                    call.respond(HttpStatusCode.Created, mapOf("message" to "User registered successfully"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to register user"))
                }
            }
        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val foundUser = UserRepository.findUserByUsername(loginRequest.username)
            if (foundUser != null && foundUser.password == loginRequest.password) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", foundUser.username)
                    .sign(algorithm)
                call.respond(HttpStatusCode.OK, AuthResponse(token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid username or password"))
            }
        }

        authenticate("auth-jwt") {
            get("/validate") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString()
                if (username != null) {
                    call.response.header("X-User-ID", username)
                    call.respond(HttpStatusCode.OK, ValidateResponse(username, "Token is valid"))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid token"))
                }
            }
        }
    }
}