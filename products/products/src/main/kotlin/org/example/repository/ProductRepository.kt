package org.example.repository

import org.example.Database
import org.example.enums.Affiliation
import org.example.models.Product
import java.sql.ResultSet

class ProductRepository {
    fun insert(product: Product): Product {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                INSERT INTO products (
                    name, affiliation, water, mass, 
                    fiber, sugar, added_sugar, 
                    saturated_fat, polyunsaturated_fat,
                    cholesterol, salt, alcohol,
                    vitamin_b7, vitamin_c, vitamin_d, vitamin_e, vitamin_k,
                    calcium, iron, zinc
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING *
            """)
            stmt.setString(1, product.name)
            stmt.setString(2, product.affiliation.name)
            stmt.setObject(3, product.water)
            stmt.setObject(4, product.mass)
            stmt.setObject(5, product.fiber)
            stmt.setObject(6, product.sugar)
            stmt.setObject(7, product.addedSugar)
            stmt.setObject(8, product.saturatedFat)
            stmt.setObject(9, product.polyunsaturatedFat)
            stmt.setObject(10, product.cholesterol)
            stmt.setObject(11, product.salt)
            stmt.setObject(12, product.alcohol)
            stmt.setObject(13, product.vitaminB7)
            stmt.setObject(14, product.vitaminC)
            stmt.setObject(15, product.vitaminD)
            stmt.setObject(16, product.vitaminE)
            stmt.setObject(17, product.vitaminK)
            stmt.setObject(18, product.calcium)
            stmt.setObject(19, product.iron)
            stmt.setObject(20, product.zinc)
            
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.toProduct() else throw IllegalStateException("Failed to create product")
        }
    }

    fun findById(id: Long): Product? {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM products WHERE id = ?")
            stmt.setLong(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.toProduct() else null
        }
    }

    fun findAll(): List<Product> {
        val products = mutableListOf<Product>()
        Database.getConnection().use { conn ->
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT * FROM products")
            while (rs.next()) {
                products.add(rs.toProduct())
            }
        }
        return products
    }

    fun deleteById(id: Long): Boolean {
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")
            stmt.setLong(1, id)
            return stmt.executeUpdate() > 0
        }
    }

    fun update(product: Product): Boolean {
        require(product.id != null) { "Product ID must not be null for update" }
        
        Database.getConnection().use { conn ->
            val stmt = conn.prepareStatement("""
                UPDATE products SET
                    name = ?,
                    affiliation = ?,
                    water = ?,
                    mass = ?,
                    fiber = ?,
                    sugar = ?,
                    added_sugar = ?,
                    saturated_fat = ?,
                    polyunsaturated_fat = ?,
                    cholesterol = ?,
                    salt = ?,
                    alcohol = ?,
                    vitamin_b7 = ?,
                    vitamin_c = ?,
                    vitamin_d = ?,
                    vitamin_e = ?,
                    vitamin_k = ?,
                    calcium = ?,
                    iron = ?,
                    zinc = ?
                WHERE id = ?
            """)
            stmt.setString(1, product.name)
            stmt.setString(2, product.affiliation.name)
            stmt.setObject(3, product.water)
            stmt.setObject(4, product.mass)
            stmt.setObject(5, product.fiber)
            stmt.setObject(6, product.sugar)
            stmt.setObject(7, product.addedSugar)
            stmt.setObject(8, product.saturatedFat)
            stmt.setObject(9, product.polyunsaturatedFat)
            stmt.setObject(10, product.cholesterol)
            stmt.setObject(11, product.salt)
            stmt.setObject(12, product.alcohol)
            stmt.setObject(13, product.vitaminB7)
            stmt.setObject(14, product.vitaminC)
            stmt.setObject(15, product.vitaminD)
            stmt.setObject(16, product.vitaminE)
            stmt.setObject(17, product.vitaminK)
            stmt.setObject(18, product.calcium)
            stmt.setObject(19, product.iron)
            stmt.setObject(20, product.zinc)
            stmt.setLong(21, product.id)
            
            return stmt.executeUpdate() > 0
        }
    }

    private fun ResultSet.toProduct(): Product {
        return Product(
            id = getLong("id"),
            name = getString("name"),
            affiliation = Affiliation.valueOf(getString("affiliation")),
            water = getDouble("water"),
            mass = getDouble("mass"),
            fiber = getDouble("fiber"),
            sugar = getDouble("sugar"),
            addedSugar = getObject("added_sugar") as Double?,
            saturatedFat = getDouble("saturated_fat"),
            polyunsaturatedFat = getObject("polyunsaturated_fat") as Double?,
            cholesterol = getObject("cholesterol") as Double?,
            salt = getDouble("salt"),
            alcohol = getObject("alcohol") as Double?,
            vitaminB7 = getObject("vitamin_b7") as Double?,
            vitaminC = getObject("vitamin_c") as Double?,
            vitaminD = getObject("vitamin_d") as Double?,
            vitaminE = getObject("vitamin_e") as Double?,
            vitaminK = getObject("vitamin_k") as Double?,
            calcium = getObject("calcium") as Double?,
            iron = getObject("iron") as Double?,
            zinc = getObject("zinc") as Double?
        )
    }
}