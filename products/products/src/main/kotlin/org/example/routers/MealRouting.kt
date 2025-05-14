package org.example.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.dto.ErrorResponse
import org.example.dto.MealDTO
import org.example.dto.MealRequestDTO
import org.example.dto.MealSummaryDTO
import org.example.service.MealService
import org.example.repository.MealsRepository
import org.example.repository.MealsContentRepository
import org.example.repository.ProductRepository
import org.example.repository.RecipeRepository
import org.example.repository.RecipeProductRepository
import org.example.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Application.configureMealRouting() {
    val mealService = MealService(
        MealsRepository(),
        MealsContentRepository(),
        ProductRepository(),
        RecipeRepository(),
        RecipeProductRepository(),
        UserRepository()
    )

    routing {
        // Create a new meal
        post("/meals") {
            try {
                val mealRequest = call.receive<MealRequestDTO>()
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@post
                }
                val createdMeal = mealService.createMeal(mealRequest, username)
                call.respond(HttpStatusCode.Created, createdMeal)
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid meal data"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to create meal: ${e.message}"))
            }
        }

        // Get all meals for a user
        get("/meals") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val meals = mealService.getAllMeals(username)
                call.respond(HttpStatusCode.OK, meals)
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid request"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve meals: ${e.message}"))
            }
        }

        // Get a specific meal by ID
        get("/meals/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID"))

                val meal = mealService.getMeal(id, username)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Meal not found"))

                call.respond(HttpStatusCode.OK, meal)
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID format"))
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid request"))
                }
            } catch (e: SecurityException) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse(e.message ?: "Access denied"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve meal: ${e.message}"))
            }
        }

        // Update a meal
        put("/meals/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@put
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID"))

                val mealRequest = call.receive<MealRequestDTO>()
                
                // Check if the meal exists and belongs to the user
                val existingMeal = mealService.getMeal(id, username)
                    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Meal not found"))
                
                if (existingMeal.username != username) {
                    return@put call.respond(HttpStatusCode.Forbidden, ErrorResponse("You don't have access to this meal"))
                }

                val updatedMeal = mealService.updateMeal(id, mealRequest, username)
                    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Failed to update meal"))

                call.respond(HttpStatusCode.OK, updatedMeal)
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid meal data"))
                }
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID format"))
            } catch (e: SecurityException) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse(e.message ?: "Access denied"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to update meal: ${e.message}"))
            }
        }

        // Delete a meal
        delete("/meals/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@delete
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID"))

                val success = mealService.deleteMeal(id, username)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Meal not found"))
                }
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID format"))
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid request"))
                }
            } catch (e: SecurityException) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse(e.message ?: "Access denied"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to delete meal: ${e.message}"))
            }
        }

        // Get meal summary (water content and KBZHU)
        get("/meals/{id}/summary") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID"))

                val summary = mealService.getMealSummary(id, username)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Meal not found"))

                call.respond(HttpStatusCode.OK, summary)
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid meal ID format"))
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid request"))
                }
            } catch (e: SecurityException) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse(e.message ?: "Access denied"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve meal summary: ${e.message}"))
            }
        }

        // Get daily meal summary
        get("/meals/daily-summary") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                
                // Parse date from query parameter, default to today if not provided
                val dateStr = call.request.queryParameters["date"]
                val date = if (dateStr != null) {
                    try {
                        LocalDate.parse(dateStr)
                    } catch (e: DateTimeParseException) {
                        return@get call.respond(
                            HttpStatusCode.BadRequest, 
                            ErrorResponse("Invalid date format. Please use ISO format (yyyy-MM-dd)")
                        )
                    }
                } else {
                    LocalDate.now()
                }

                val summary = mealService.getDailySummary(date, username)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("No meals found for this date"))
                call.respond(HttpStatusCode.OK, summary)
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("User with username") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "User not found"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid request"))
                }
            } catch (e: SecurityException) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse(e.message ?: "Access denied"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve daily summary: ${e.message}"))
            }
        }
    }
} 