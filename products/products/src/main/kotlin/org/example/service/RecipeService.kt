package org.example.service

import org.example.dto.RecipeDTO
import org.example.dto.RecipeRequestDTO
import org.example.enums.Affiliation
import org.example.mappers.toDTO
import org.example.mappers.toEntity
import org.example.models.Product
import org.example.models.Recipe
import org.example.models.RecipeProduct
import org.example.repository.ProductRepository
import org.example.repository.RecipeProductRepository
import org.example.repository.RecipeRepository
import org.example.repository.UserProductRepository

class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val recipeProductRepository: RecipeProductRepository,
    private val productRepository: ProductRepository,
    private val userProductRepository: UserProductRepository
) {
    fun createRecipe(recipeRequest: RecipeRequestDTO, username: String): RecipeDTO {
        // Validate products
        recipeRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")
            println(product)

            // Check if the product is either:
            // 1. A regional product (not USER)
            // 2. A user product belonging to the same user
            if (product.affiliation == Affiliation.USER) {
                val productOwner = userProductRepository.findByProductId(product.id!!)
                    ?: throw IllegalArgumentException("Product ${product.id} is marked as USER but has no owner")
                
                if (productOwner != username) {
                    throw IllegalArgumentException("Product ${product.id} belongs to a different user")
                }
            }
        }

        val recipe = recipeRequest.toEntity(username)
        println(recipe)
        val recipeId = recipeRepository.insert(recipe)
        println(recipeId)

        // Now that we have the recipe ID, we can insert the recipe products
        recipeRequest.productIds.forEach { productId ->
            recipeProductRepository.insert(RecipeProduct(recipeId, productId))
        }

        return getRecipe(recipeId)!!
    }

    fun getRecipe(id: Long): RecipeDTO? {
        val recipe = recipeRepository.findById(id) ?: return null
        val recipeProducts = recipeProductRepository.findByRecipeId(id)
        val products = recipeProducts.mapNotNull { recipeProduct ->
            productRepository.findById(recipeProduct.productId)
        }
        return recipe.toDTO(products)
    }

    fun getRecipesByUsername(username: String): List<RecipeDTO> {
        return recipeRepository.findByUsername(username).map { recipe ->
            val recipeProducts = recipeProductRepository.findByRecipeId(recipe.id!!)
            val products = recipeProducts.mapNotNull { recipeProduct ->
                productRepository.findById(recipeProduct.productId)
            }
            recipe.toDTO(products)
        }
    }

    fun deleteRecipe(id: Long): Boolean {
        recipeProductRepository.deleteByRecipeId(id)
        return recipeRepository.deleteById(id)
    }

    fun updateRecipe(recipeRequest: RecipeRequestDTO, id: Long, username: String): RecipeDTO? {
        val existingRecipe = recipeRepository.findById(id) ?: return null
        
        // Validate products
        recipeRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")

            if (product.affiliation == Affiliation.USER) {
                val productOwner = userProductRepository.findByProductId(product.id!!)
                    ?: throw IllegalArgumentException("Product ${product.id} is marked as USER but has no owner")
                
                if (productOwner != username) {
                    throw IllegalArgumentException("Product ${product.id} belongs to a different user")
                }
            }
        }

        val recipe = recipeRequest.toEntity(username).copy(id = id)

        if (!recipeRepository.update(recipe)) {
            return null
        }

        // Delete existing products and add new ones
        recipeProductRepository.deleteByRecipeId(recipe.id!!)
        
        // Add new products
        recipeRequest.productIds.forEach { productId ->
            recipeProductRepository.insert(RecipeProduct(recipe.id!!, productId))
        }

        return getRecipe(recipe.id!!)
    }
} 