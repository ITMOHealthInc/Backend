package org.example.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.dto.ErrorResponse
import org.example.dto.RecipeRequestDTO
import org.example.repository.ProductRepository
import org.example.repository.RecipeProductRepository
import org.example.repository.RecipeRepository
import org.example.repository.UserProductRepository
import org.example.service.RecipeService

fun Application.configureRecipeRouting() {
    val recipeRepository = RecipeRepository()
    val recipeProductRepository = RecipeProductRepository()
    val productRepository = ProductRepository()
    val userProductRepository = UserProductRepository()
    val recipeService = RecipeService(recipeRepository, recipeProductRepository, productRepository, userProductRepository)

    routing {
        
        post("/recipes") {
            try {
                val recipeRequest = call.receive<RecipeRequestDTO>()
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@post
                }
                val createdRecipe = recipeService.createRecipe(recipeRequest, username)
                call.respond(HttpStatusCode.Created, createdRecipe)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid recipe data"))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to create recipe: ${e.message}"))
            }
        }

        
        get("/recipes") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val recipes = recipeService.getRecipesByUsername(username)
                call.respond(HttpStatusCode.OK, recipes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve recipes: ${e.message}"))
            }
        }

        
        get("/recipes/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid recipe ID"))

                val recipe = recipeService.getRecipe(id, username)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Recipe not found"))

                call.respond(HttpStatusCode.OK, recipe)
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid recipe ID format"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve recipe: ${e.message}"))
            }
        }

        
        put("/recipes/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@put
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid recipe ID"))

                val recipeRequest = call.receive<RecipeRequestDTO>()
                
                
                val existingRecipe = recipeService.getRecipe(id, username)
                    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Recipe not found"))
                
                if (existingRecipe.username != username) {
                    return@put call.respond(HttpStatusCode.Forbidden, ErrorResponse("You don't have access to this recipe"))
                }

                val updatedRecipe = recipeService.updateRecipe(recipeRequest, id, username)
                    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Failed to update recipe"))

                call.respond(HttpStatusCode.OK, updatedRecipe)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid recipe data"))
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid recipe ID format"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to update recipe: ${e.message}"))
            }
        }

        
        delete("/recipes/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@delete
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid recipe ID"))

                
                val existingRecipe = recipeService.getRecipe(id, username)
                    ?: return@delete call.respond(HttpStatusCode.NotFound, ErrorResponse("Recipe not found"))
                
                if (existingRecipe.username != username) {
                    return@delete call.respond(HttpStatusCode.Forbidden, ErrorResponse("You don't have access to this recipe"))
                }

                val success = recipeService.deleteRecipe(id, username)
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Failed to delete recipe"))
                }
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid recipe ID format"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to delete recipe: ${e.message}"))
            }
        }
    }
} 