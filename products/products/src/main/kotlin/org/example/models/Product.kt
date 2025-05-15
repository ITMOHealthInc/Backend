package org.example.models

import kotlinx.serialization.Serializable
import org.example.dto.Kbzhu
import org.example.enums.Affiliation

@Serializable
data class Product(
    val id: Long? = null,
    val name: String,
    val affiliation: Affiliation,
    val water: Double? = null,
    val mass: Double? = null,

    val proteins: Double? = null,

    val fiber: Double? = null,
    val sugar: Double? = null,
    val addedSugar: Double? = null,

    val saturatedFat: Double? = null,
    val polyunsaturatedFat: Double? = null,

    val cholesterol: Double? = null,
    val salt: Double? = null,
    val alcohol: Double? = null,

    val vitaminB7: Double? = null,
    val vitaminC: Double? = null,
    val vitaminD: Double? = null,
    val vitaminE: Double? = null,
    val vitaminK: Double? = null,

    val calcium: Double? = null,
    val iron: Double? = null,
    val zinc: Double? = null
) {
    fun calculateKbzhu(): Kbzhu {
        
        val proteins = this.proteins ?: 0.0

        
        val fiber = this.fiber ?: 0.0
        val sugar = this.sugar ?: 0.0
        val carbohydrates = fiber + sugar

        
        val saturatedFat = this.saturatedFat ?: 0.0
        val polyunsaturatedFat = this.polyunsaturatedFat ?: 0.0
        val fats = saturatedFat + polyunsaturatedFat

        
        val alcohol = this.alcohol ?: 0.0

        
        val calories = (proteins * 4) + (carbohydrates * 4) + (fats * 9) + (alcohol * 7)

        return Kbzhu(
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbohydrates = carbohydrates
        )
    }
}
