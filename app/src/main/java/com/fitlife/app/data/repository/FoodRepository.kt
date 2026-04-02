package com.fitlife.app.data.repository

import com.fitlife.app.data.api.OpenFoodAPI
import com.fitlife.app.data.api.UsdaFoodAPI
import com.fitlife.app.data.database.FoodDao
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.utils.toFoodEntry

class FoodRepository(
    private val foodDao: FoodDao,
    private val openFoodAPI: OpenFoodAPI,
    private val usdaFoodAPI: UsdaFoodAPI,
    private val usdaApiKey: String
) {
    // Local db functions
    suspend fun addFood(food: FoodEntry): Long = foodDao.insertFood(food)
    suspend fun getFood(): List<FoodEntry>      = foodDao.getFood()
    suspend fun getFoodById(id: Int): FoodEntry = foodDao.getFoodById(id)
    suspend fun updateFood(food: FoodEntry)     = foodDao.updateFood(food)
    suspend fun deleteFood(food: FoodEntry)     = foodDao.deleteFood(food)

    // USDA name search
    suspend fun searchByName(query: String): List<FoodEntry> {
        val response = usdaFoodAPI.searchFood(query, usdaApiKey)

        if (!response.isSuccessful) {
            throw Exception("USDA search failed: HTTP ${response.code()} - ${response.errorBody()?.string()}")
        }

        val foods = response.body()?.foods ?: throw Exception("USDA returned null foods list")

        return foods
            .filter { it.description?.isNotBlank() == true && it.calories() > 0f }
            .map { it.toFoodEntry(mealId = 0) }
            .distinctBy { it.foodName }   // dedupe same food from multiple data sources
    }
    // OpenFoodFacts barcode lookup
    suspend fun searchByBarcode(barcode: String): FoodEntry {
        val response = openFoodAPI.getProductByBarcode(barcode)

        if (!response.isSuccessful) {
            throw Exception("Barcode lookup failed: HTTP ${response.code()}")
        }

        val body = response.body()
        if (body?.status == 0 || body?.product == null) {
            throw Exception("Product not found")
        }

        return body.product.toFoodEntry(mealId = 0)
    }
}