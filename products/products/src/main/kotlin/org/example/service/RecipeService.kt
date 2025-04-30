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
import org.example.repository.UserRepository

class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val recipeProductRepository: RecipeProductRepository,
    private val productRepository: ProductRepository,
    private val userProductRepository: UserProductRepository,
    private val userRepository: UserRepository = UserRepository()
) {
    fun createRecipe(recipeRequest: RecipeRequestDTO, username: String): RecipeDTO {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        // Validate products
        recipeRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")

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
        val recipeId = recipeRepository.insert(recipe)

        // Now that we have the recipe ID, we can insert the recipe products
        recipeRequest.productIds.forEach { productId ->
            recipeProductRepository.insert(RecipeProduct(recipeId, productId))
        }

        return getRecipe(recipeId, username)!!
    }

    fun getRecipe(id: Long, username: String): RecipeDTO? {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val recipe = recipeRepository.findById(id) ?: return null
        
        // Check if the recipe belongs to the user
        if (recipe.username != username) {
            throw SecurityException("You don't have permission to access this recipe")
        }
        
        val recipeProducts = recipeProductRepository.findByRecipeId(id)
        val products = recipeProducts.mapNotNull { recipeProduct ->
            val product = productRepository.findById(recipeProduct.productId)
            if (product?.affiliation == Affiliation.USER) {
                val productOwner = userProductRepository.findByProductId(product.id!!)
                    ?: throw SecurityException("Product ${product.id} is marked as USER but has no owner")
                if (productOwner != username) {
                    throw SecurityException("You don't have permission to access product ${product.id} in this recipe")
                }
            }
            product
        }
        return recipe.toDTO(products)
    }

    fun getRecipesByUsername(username: String): List<RecipeDTO> {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        return recipeRepository.findByUsername(username).map { recipe ->
            val recipeProducts = recipeProductRepository.findByRecipeId(recipe.id!!)
            val products = recipeProducts.mapNotNull { recipeProduct ->
                val product = productRepository.findById(recipeProduct.productId)
                if (product?.affiliation == Affiliation.USER) {
                    val productOwner = userProductRepository.findByProductId(product.id!!)
                        ?: throw SecurityException("Product ${product.id} is marked as USER but has no owner")
                    if (productOwner != username) {
                        throw SecurityException("You don't have permission to access product ${product.id} in this recipe")
                    }
                }
                product
            }
            recipe.toDTO(products)
        }
    }

    fun deleteRecipe(id: Long, username: String): Boolean {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val recipe = recipeRepository.findById(id) ?: return false
        
        // Check if the recipe belongs to the user
        if (recipe.username != username) {
            throw SecurityException("You don't have permission to delete this recipe")
        }
        
        // Check if all products in the recipe are accessible
        val recipeProducts = recipeProductRepository.findByRecipeId(id)
        recipeProducts.forEach { recipeProduct ->
            val product = productRepository.findById(recipeProduct.productId)
            if (product?.affiliation == Affiliation.USER) {
                val productOwner = userProductRepository.findByProductId(product.id!!)
                    ?: throw SecurityException("Product ${product.id} is marked as USER but has no owner")
                if (productOwner != username) {
                    throw SecurityException("You don't have permission to access product ${product.id} in this recipe")
                }
            }
        }
        
        recipeProductRepository.deleteByRecipeId(id)
        return recipeRepository.deleteById(id)
    }

    fun updateRecipe(recipeRequest: RecipeRequestDTO, id: Long, username: String): RecipeDTO? {
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val existingRecipe = recipeRepository.findById(id) ?: return null
        
        // Check if the recipe belongs to the user
        if (existingRecipe.username != username) {
            throw SecurityException("You don't have permission to update this recipe")
        }
        
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

        return getRecipe(recipe.id!!, username)
    }
} 