package com.fitlife.app.data.repository

import com.fitlife.app.data.api.FoodAPI
import com.fitlife.app.data.database.FoodDao
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.utils.toFoodEntry

class FoodRepository(private val foodDao: FoodDao, private val foodAPI: FoodAPI) {
    // Local database
    suspend fun addFood(food: FoodEntry): Long = foodDao.insertFood(food)
    suspend fun getFood(): List<FoodEntry> = foodDao.getFood()
    suspend fun getFoodById(id: Int): FoodEntry = foodDao.getFoodById(id)
    suspend fun updateFood(food: FoodEntry) = foodDao.updateFood(food)
    suspend fun deleteFood(food: FoodEntry) = foodDao.deleteFood(food)

    // Remote API
    suspend fun searchByName(query: String): List<FoodEntry> {
        val response = foodAPI.searchFood(query)

        return response.products.map {
            it.toFoodEntry(mealId = 0)
        }
    }
    suspend fun searchByBarcode(barcode: String): FoodEntry {
        val response = foodAPI.getProductByBarcode(barcode)

        return response.product?.toFoodEntry(mealId = 0)
            ?: throw Exception("Product not found")
    }
}