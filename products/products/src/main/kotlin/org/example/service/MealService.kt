package org.example.service

import org.example.dto.MealDTO
import org.example.dto.MealRequestDTO
import org.example.dto.MealSummaryDTO
import org.example.dto.ProductDTO
import org.example.dto.RecipeDTO
import org.example.dto.Kbzhu
import org.example.enums.TypesMeals
import org.example.enums.TypesMealsContent
import org.example.mappers.toDTO
import org.example.models.Meals
import org.example.models.MealsContent
import org.example.models.Product
import org.example.models.Recipe
import org.example.repository.MealsContentRepository
import org.example.repository.MealsRepository
import org.example.repository.ProductRepository
import org.example.repository.RecipeRepository
import org.example.repository.RecipeProductRepository
import org.example.repository.UserRepository
import org.example.repository.UserProductRepository
import org.example.enums.Affiliation
import java.time.LocalDateTime

class MealService(
    private val mealsRepository: MealsRepository,
    private val mealsContentRepository: MealsContentRepository,
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
    private val recipeProductRepository: RecipeProductRepository,
    private val userRepository: UserRepository,
    private val userProductRepository: UserProductRepository = UserProductRepository()
) {
    fun createMeal(mealRequest: MealRequestDTO, username: String): MealDTO {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        // Validate products access
        mealRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")
            
            // Check if the product is either:
            // 1. A general product (not USER)
            // 2. A user product belonging to the same user
            if (product.affiliation == Affiliation.USER) {
                val productOwner = userProductRepository.findByProductId(product.id!!)
                    ?: throw IllegalArgumentException("Product ${product.id} is marked as USER but has no owner")
                
                if (productOwner != username) {
                    throw IllegalArgumentException("Product ${product.id} belongs to a different user")
                }
            }
        }

        // Validate recipes access
        mealRequest.recipeIds.forEach { recipeId ->
            val recipe = recipeRepository.findById(recipeId)
                ?: throw IllegalArgumentException("Recipe with id $recipeId not found")
            
            // Check if the recipe belongs to the user
            if (recipe.username != username) {
                throw IllegalArgumentException("Recipe ${recipe.id} belongs to a different user")
            }
        }

        // Create the meal
        val meal = Meals(
            id = 0, // Will be set by the database
            type = mealRequest.type,
            addedAt = LocalDateTime.now(),
            username = username
        )
        val createdMeal = mealsRepository.insert(meal)

        // Add products to the meal
        mealRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")
            
            mealsContentRepository.insert(MealsContent(
                mealId = createdMeal.id,
                contentId = productId,
                typeContent = TypesMealsContent.PRODUCT
            ))
        }

        // Add recipes to the meal
        mealRequest.recipeIds.forEach { recipeId ->
            val recipe = recipeRepository.findById(recipeId)
                ?: throw IllegalArgumentException("Recipe with id $recipeId not found")
            
            mealsContentRepository.insert(MealsContent(
                mealId = createdMeal.id,
                contentId = recipeId,
                typeContent = TypesMealsContent.RECIPE
            ))
        }

        return getMeal(createdMeal.id, username)!!
    }

    fun getMeal(id: Long, username: String): MealDTO? {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val meal = mealsRepository.findById(id) ?: return null
        
        // Check if the meal belongs to the user
        if (meal.username != username) {
            throw SecurityException("You don't have permission to access this meal")
        }
        
        val contents = mealsContentRepository.findByMealId(id)
        
        val productIds = contents
            .filter { it.typeContent == TypesMealsContent.PRODUCT }
            .map { it.contentId }
        val recipeIds = contents
            .filter { it.typeContent == TypesMealsContent.RECIPE }
            .map { it.contentId }
        
        val products = productIds.mapNotNull { productRepository.findById(it) }
            .map { it.toDTO() }
        
        // Get recipes with their products
        val recipes = recipeIds.mapNotNull { recipeId ->
            val recipe = recipeRepository.findById(recipeId) ?: return@mapNotNull null
            val recipeProductIds = recipeProductRepository.findByRecipeId(recipeId)
                .map { it.productId }
            val recipeProducts = recipeProductIds.mapNotNull { productRepository.findById(it) }
            recipe.toDTO(recipeProducts)
        }
        
        return MealDTO(
            id = meal.id,
            type = meal.type,
            addedAt = meal.addedAt,
            username = meal.username,
            products = products,
            recipes = recipes
        )
    }

    fun getMealsByUsername(username: String): List<MealDTO> {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        return mealsRepository.findByUsername(username).mapNotNull { getMeal(it.id, username) }
    }

    fun getAllMeals(username: String): List<MealDTO> {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        return mealsRepository.findByUsername(username).mapNotNull { getMeal(it.id, username) }
    }

    fun deleteMeal(id: Long, username: String): Boolean {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val meal = mealsRepository.findById(id) ?: return false
        
        // Check if the meal belongs to the user
        if (meal.username != username) {
            throw SecurityException("You don't have permission to delete this meal")
        }
        
        mealsContentRepository.deleteByMealId(id)
        return mealsRepository.deleteById(id)
    }

    fun updateMeal(id: Long, mealRequest: MealRequestDTO, username: String): MealDTO? {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val existingMeal = mealsRepository.findById(id) ?: return null
        
        // Check if the meal belongs to the user
        if (existingMeal.username != username) {
            throw SecurityException("You don't have permission to update this meal")
        }

        // Validate products
        mealRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")
        }

        // Validate recipes
        mealRequest.recipeIds.forEach { recipeId ->
            val recipe = recipeRepository.findById(recipeId)
                ?: throw IllegalArgumentException("Recipe with id $recipeId not found")
        }

        // Update the meal
        val updatedMeal = Meals(
            id = id,
            type = mealRequest.type,
            addedAt = existingMeal.addedAt, // Keep the original addedAt
            username = username
        )

        if (!mealsRepository.update(updatedMeal)) {
            return null
        }

        // Delete existing contents and add new ones
        mealsContentRepository.deleteByMealId(id)

        // Add new products
        mealRequest.productIds.forEach { productId ->
            mealsContentRepository.insert(MealsContent(
                mealId = id,
                contentId = productId,
                typeContent = TypesMealsContent.PRODUCT
            ))
        }

        // Add new recipes
        mealRequest.recipeIds.forEach { recipeId ->
            mealsContentRepository.insert(MealsContent(
                mealId = id,
                contentId = recipeId,
                typeContent = TypesMealsContent.RECIPE
            ))
        }

        return getMeal(id, username)
    }

    fun getMealSummary(id: Long, username: String): MealSummaryDTO? {
        // Check if user exists
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val meal = mealsRepository.findById(id) ?: return null
        
        // Check if the meal belongs to the user
        if (meal.username != username) {
            throw SecurityException("You don't have permission to access this meal")
        }
        
        val contents = mealsContentRepository.findByMealId(id)
        
        val productIds = contents
            .filter { it.typeContent == TypesMealsContent.PRODUCT }
            .map { it.contentId }
        val recipeIds = contents
            .filter { it.typeContent == TypesMealsContent.RECIPE }
            .map { it.contentId }
        
        val products = productIds.mapNotNull { productRepository.findById(it) }
        val recipes = recipeIds.mapNotNull { recipeId ->
            val recipe = recipeRepository.findById(recipeId) ?: return@mapNotNull null
            val recipeProductIds = recipeProductRepository.findByRecipeId(recipeId)
                .map { it.productId }
            recipeProductIds.mapNotNull { productRepository.findById(it) }
        }.flatten()
        
        // Calculate total water content
        val totalWater = (products + recipes).sumOf { it.water ?: 0.0 }
        
        // Calculate total KBZHU
        val totalKbzhu = (products + recipes).fold(Kbzhu(0.0, 0.0, 0.0, 0.0)) { acc, product ->
            val kbzhu = product.calculateKbzhu()
            Kbzhu(
                calories = acc.calories + kbzhu.calories,
                proteins = acc.proteins + kbzhu.proteins,
                fats = acc.fats + kbzhu.fats,
                carbohydrates = acc.carbohydrates + kbzhu.carbohydrates
            )
        }
        
        return MealSummaryDTO(totalWater, totalKbzhu)
    }
} 