package org.example.routers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import org.example.dto.ErrorResponse
import org.example.dto.ProductDTO
import org.example.mappers.toDTO
import org.example.mappers.toEntity
import org.example.repository.ProductRepository
import org.example.repository.UserProductRepository
import org.example.service.ProductService

fun Application.configureProductsRouting() {
    val productRepository = ProductRepository()
    val userProductRepository = UserProductRepository()
    val productService = ProductService(productRepository, userProductRepository)

    install(ContentNegotiation) {
        json()
    }

    routing {
        // Create a new product
        post("/products") {
            try {
                val productDTO = call.receive<ProductDTO>()
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@post
                }
                val createdProduct = productService.createProduct(productDTO.toEntity(), username)
                call.respond(HttpStatusCode.Created, createdProduct.toDTO())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid product data"))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to create product: ${e.message}"))
            }
        }

        // Get all products
        get("/products") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val products = productService.getAllProducts(username)
                call.respond(HttpStatusCode.OK, products.map { it.toDTO() })
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve products: ${e.message}"))
            }
        }

        // Get a specific product by ID
        get("/products/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@get
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID"))

                val product = productService.getProduct(id, username)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Product not found"))

                call.respond(HttpStatusCode.OK, product.toDTO())
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID format"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to retrieve product: ${e.message}"))
            }
        }

        // Update a product
        put("/products/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@put
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID"))

                val productDTO = call.receive<ProductDTO>()
                val product = productDTO.toEntity().copy(id = id)

                val updatedProduct = productService.updateProduct(product, username)
                    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Product not found"))

                call.respond(HttpStatusCode.OK, updatedProduct.toDTO())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid product data"))
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID format"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to update product: ${e.message}"))
            }
        }

        // Delete a product
        delete("/products/{id}") {
            try {
                val username = call.request.headers["X-User-ID"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing X-User-ID header"))
                    return@delete
                }
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID"))

                val success = productService.deleteProduct(id, username)
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Product not found"))
                }
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID format"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to delete product: ${e.message}"))
            }
        }
    }
} 