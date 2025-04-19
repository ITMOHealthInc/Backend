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
        get("/steps") {
            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@get
            }
            try {
                val todaySteps = stepsService.getTodaySteps(username)
                call.respond(HttpStatusCode.OK, todaySteps)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to get today steps: ${e.message}"))
            }
        }

        post("/set-steps") {
            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@post
            }
            val request = call.receive<SetStepsRequest>()

            try {
                val todaySteps = stepsService.setSteps(username, request.steps)
                call.respond(HttpStatusCode.OK, todaySteps)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to set steps: ${e.message}"))
            }
        }

        post("/update-goal") {
            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@post
            }
            val request = call.receive<UpdateGoalRequest>()

            try {
                val todaySteps = stepsService.updateGoal(username, request.goal)
                call.respond(HttpStatusCode.OK, todaySteps)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to update goal: ${e.message}"))
            }
        }

        get("/month-steps") {
            val username = call.request.headers["X-User-ID"] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                return@get
            }

            val month = call.request.queryParameters["month"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid 'month' query parameter"))
                return@get
            }
            val year = call.request.queryParameters["year"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing or invalid 'year' query parameter"))
                return@get
            }

            try {
                val monthSteps = stepsService.getMonthSteps(username, month, year)
                call.respond(HttpStatusCode.OK, monthSteps)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to get month steps: ${e.message}"))
            }
        }
    }
}