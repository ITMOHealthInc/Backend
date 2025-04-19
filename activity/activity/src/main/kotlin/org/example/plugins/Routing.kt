package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import org.example.models.*
import org.example.repository.StepsRepository
import org.example.service.StepsService
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

fun Application.configureRouting() {

    val stepsRepository = StepsRepository()
    val stepsService = StepsService(stepsRepository)

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@get
            }
            call.respond(HttpStatusCode.OK, username)
        }
        get("/steps") {

            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@get
            }
            try {
                val steps = stepsService.getSteps(username)
                call.respond(HttpStatusCode.OK, mapOf("steps" to steps))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.message ?: "Unknown error"))
            }
        }

        post("/set-steps") {
            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@post
            }
            val request = call.receive<SetStepsRequest>()

            try {
                stepsService.setSteps(username, request.steps)
                call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.message ?: "Unknown error"))
            }
        }
    }
}