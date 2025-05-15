package org.example.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.dto.UserGoalDto
import org.example.dto.UserGoalRequestDto
import org.example.service.GoalAlreadyExistsException
import org.example.service.GoalNotFoundException
import org.example.service.UnauthorizedAccessException
import org.example.service.UserGoalService
import org.example.service.UserNotFoundException

fun Application.configureUserGoalRouting() {
    val userGoalService = UserGoalService()
    
    routing {
        post("/user-goals") {
            try {
                val requestDto = call.receive<UserGoalRequestDto>()
                val userId = call.request.headers["X-User-ID"] 
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "X-User-ID header is required")
                
                val createdGoal = userGoalService.createUserGoal(requestDto, userId)
                call.respond(HttpStatusCode.Created, createdGoal)
            } catch (e: GoalAlreadyExistsException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "User not found")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to (e.message ?: "Error creating goal")))
            }
        }
        
        
        get("/user-goals") {
            try {
                val userId = call.request.headers["X-User-ID"] 
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "X-User-ID header is required")
                
                val goal = userGoalService.getUserGoal(userId)
                call.respond(goal)
            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "User not found")))
            } catch (e: GoalNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "Goal not found for user")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error retrieving goal")))
            }
        }
        
        
        put("/user-goals") {
            try {
                val userId = call.request.headers["X-User-ID"] 
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, "X-User-ID header is required")
                
                val requestDto = call.receive<UserGoalRequestDto>()
                
                val updatedGoal = userGoalService.updateUserGoal(requestDto, userId)
                call.respond(updatedGoal)
            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "User not found")))
            } catch (e: GoalNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "Goal not found")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to (e.message ?: "Error updating goal")))
            }
        }
        
        
        delete("/user-goals") {
            try {
                val userId = call.request.headers["X-User-ID"] 
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "X-User-ID header is required")
                
                userGoalService.deleteUserGoal(userId)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "User not found")))
            } catch (e: GoalNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to (e.message ?: "Goal not found")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error deleting goal")))
            }
        }

    }
} 