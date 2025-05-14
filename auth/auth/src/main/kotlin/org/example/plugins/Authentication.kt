package org.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

data class User(val username: String, val password: String)

val users = mutableListOf<User>()

fun Application.configureAuthentication() {
    val secret = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET not found in environment variables")
    val issuer = System.getenv("JWT_ISSUER") ?: throw IllegalStateException("JWT_ISSUER not found in environment variables")
    val audience = System.getenv("JWT_AUDIENCE") ?: throw IllegalStateException("JWT_AUDIENCE not found in environment variables")
    val myRealm = System.getenv("JWT_REALM") ?: throw IllegalStateException("JWT_REALM not found in environment variables")

    val algorithm = Algorithm.HMAC256(secret)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(algorithm)
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
}