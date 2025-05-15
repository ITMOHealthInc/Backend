package org.example.service

import org.example.dto.MealDTO
import org.example.dto.MealRequestDTO
import org.example.dto.MealSummaryDTO
import org.example.dto.DailySummaryDTO
import org.example.dto.MonthlySummaryDTO
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
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

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
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        
        mealRequest.productIds.forEach { productId ->
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

        
        mealRequest.recipeIds.forEach { recipeId ->
            val recipe = recipeRepository.findById(recipeId)
                ?: throw IllegalArgumentException("Recipe with id $recipeId not found")
            
            
            if (recipe.username != username) {
                throw IllegalArgumentException("Recipe ${recipe.id} belongs to a different user")
            }
        }

        
        val meal = Meals(
            id = 0, 
            type = mealRequest.type,
            addedAt = LocalDateTime.now(),
            username = username
        )
        val createdMeal = mealsRepository.insert(meal)

        
        mealRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")
            
            mealsContentRepository.insert(MealsContent(
                mealId = createdMeal.id,
                contentId = productId,
                typeContent = TypesMealsContent.PRODUCT
            ))
        }

        
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
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val meal = mealsRepository.findById(id) ?: return null
        
        
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
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        return mealsRepository.findByUsername(username).mapNotNull { getMeal(it.id, username) }
    }

    fun getAllMeals(username: String): List<MealDTO> {
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        return mealsRepository.findByUsername(username).mapNotNull { getMeal(it.id, username) }
    }

    fun deleteMeal(id: Long, username: String): Boolean {
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val meal = mealsRepository.findById(id) ?: return false
        
        
        if (meal.username != username) {
            throw SecurityException("You don't have permission to delete this meal")
        }
        
        mealsContentRepository.deleteByMealId(id)
        return mealsRepository.deleteById(id)
    }

    fun updateMeal(id: Long, mealRequest: MealRequestDTO, username: String): MealDTO? {
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val existingMeal = mealsRepository.findById(id) ?: return null
        
        
        if (existingMeal.username != username) {
            throw SecurityException("You don't have permission to update this meal")
        }

        
        mealRequest.productIds.forEach { productId ->
            val product = productRepository.findById(productId)
                ?: throw IllegalArgumentException("Product with id $productId not found")
        }

        
        mealRequest.recipeIds.forEach { recipeId ->
            val recipe = recipeRepository.findById(recipeId)
                ?: throw IllegalArgumentException("Recipe with id $recipeId not found")
        }

        
        val updatedMeal = Meals(
            id = id,
            type = mealRequest.type,
            addedAt = existingMeal.addedAt, 
            username = username
        )

        if (!mealsRepository.update(updatedMeal)) {
            return null
        }

        
        mealsContentRepository.deleteByMealId(id)

        
        mealRequest.productIds.forEach { productId ->
            mealsContentRepository.insert(MealsContent(
                mealId = id,
                contentId = productId,
                typeContent = TypesMealsContent.PRODUCT
            ))
        }

        
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
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        val meal = mealsRepository.findById(id) ?: return null
        
        
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
        
        
        val totalWater = (products + recipes).sumOf { it.water ?: 0.0 }
        
        
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
    
    fun getDailySummary(date: LocalDate, username: String): DailySummaryDTO? {
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        
        
        val allMeals = mealsRepository.findByUsername(username)
        
        
        val mealsForDate = allMeals.filter { 
            val mealDate = it.addedAt.toLocalDate()
            mealDate.isEqual(date)
        }
        
        
        if (mealsForDate.isEmpty()) {
            return DailySummaryDTO(
                date = date.toString(),
                totalWater = 0.0,
                totalKbzhu = Kbzhu(0.0, 0.0, 0.0, 0.0)
            )
        }
        
        
        var totalWater = 0.0
        var totalCalories = 0.0
        var totalProteins = 0.0
        var totalFats = 0.0
        var totalCarbohydrates = 0.0
        
        for (meal in mealsForDate) {
            val mealSummary = getMealSummary(meal.id, username) ?: continue
            
            
            totalWater += mealSummary.totalWater
            totalCalories += mealSummary.totalKbzhu.calories
            totalProteins += mealSummary.totalKbzhu.proteins
            totalFats += mealSummary.totalKbzhu.fats
            totalCarbohydrates += mealSummary.totalKbzhu.carbohydrates
        }
        
        return DailySummaryDTO(
            date = date.toString(),
            totalWater = totalWater,
            totalKbzhu = Kbzhu(
                calories = totalCalories,
                proteins = totalProteins,
                fats = totalFats,
                carbohydrates = totalCarbohydrates
            )
        )
    }

    fun createWaterMeal(waterAmount: Long, username: String): MealDTO {
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }

        
        val roundedWaterAmount = ((waterAmount / 50.0).roundToInt() * 50).toLong()

        
        val productId = (roundedWaterAmount / 50).toInt()
        
        
        if (productId < 1 || productId > 45) {
            throw IllegalArgumentException("Water amount results in invalid product ID: $productId. Valid range is 1-45 (50-2250ml).")
        }

        
        val product = productRepository.findById(productId.toLong())
            ?: throw IllegalArgumentException("Water product with id $productId not found")

        
        val meal = Meals(
            id = 0, 
            type = TypesMeals.SNACK, 
            addedAt = LocalDateTime.now(),
            username = username
        )
        val createdMeal = mealsRepository.insert(meal)

        
        mealsContentRepository.insert(MealsContent(
            mealId = createdMeal.id,
            contentId = productId.toLong(),
            typeContent = TypesMealsContent.PRODUCT
        ))

        return getMeal(createdMeal.id, username)!!
    }
    
    fun getMonthlySummary(year: Int, month: Int, username: String): MonthlySummaryDTO? {
        
        if (!userRepository.exists(username)) {
            throw IllegalArgumentException("User with username $username does not exist")
        }
        
        
        if (month < 1 || month > 12) {
            throw IllegalArgumentException("Invalid month: $month. Month must be between 1 and 12.")
        }
        
        
        val allMeals = mealsRepository.findByUsername(username)
        
        
        val mealsForMonth = allMeals.filter { 
            val mealDate = it.addedAt.toLocalDate()
            mealDate.year == year && mealDate.monthValue == month
        }
        
        
        val daysInMonth = getDaysInMonth(year, month)
        val mealsByDay = mealsForMonth.groupBy { it.addedAt.toLocalDate() }
        
        
        var totalWater = 0.0
        var totalCalories = 0.0
        var totalProteins = 0.0
        var totalFats = 0.0
        var totalCarbohydrates = 0.0
        
        
        val dailySummaries = mutableListOf<DailySummaryDTO>()
        
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            val mealsForDay = mealsByDay[date] ?: emptyList()
            
            if (mealsForDay.isEmpty()) {
                
                dailySummaries.add(
                    DailySummaryDTO(
                        date = date.toString(),
                        totalWater = 0.0,
                        totalKbzhu = Kbzhu(0.0, 0.0, 0.0, 0.0)
                    )
                )
                continue
            }
            
            
            var dailyWater = 0.0
            var dailyCalories = 0.0
            var dailyProteins = 0.0
            var dailyFats = 0.0
            var dailyCarbohydrates = 0.0
            
            
            for (meal in mealsForDay) {
                val mealSummary = getMealSummary(meal.id, username) ?: continue
                
                
                dailyWater += mealSummary.totalWater
                dailyCalories += mealSummary.totalKbzhu.calories
                dailyProteins += mealSummary.totalKbzhu.proteins
                dailyFats += mealSummary.totalKbzhu.fats
                dailyCarbohydrates += mealSummary.totalKbzhu.carbohydrates
            }
            
            
            val dailySummary = DailySummaryDTO(
                date = date.toString(),
                totalWater = dailyWater,
                totalKbzhu = Kbzhu(
                    calories = dailyCalories,
                    proteins = dailyProteins,
                    fats = dailyFats,
                    carbohydrates = dailyCarbohydrates
                )
            )
            
            
            dailySummaries.add(dailySummary)
            
            
            totalWater += dailyWater
            totalCalories += dailyCalories
            totalProteins += dailyProteins
            totalFats += dailyFats
            totalCarbohydrates += dailyCarbohydrates
        }
        
        
        return MonthlySummaryDTO(
            year = year,
            month = month,
            totalWater = totalWater,
            totalKbzhu = Kbzhu(
                calories = totalCalories,
                proteins = totalProteins,
                fats = totalFats,
                carbohydrates = totalCarbohydrates
            ),
            dailySummaries = dailySummaries
        )
    }
    
    
    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }
    
    
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
} 