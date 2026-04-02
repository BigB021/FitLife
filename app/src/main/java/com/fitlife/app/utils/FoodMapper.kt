package com.fitlife.app.utils

import com.fitlife.app.data.dto.ProductDto
import com.fitlife.app.data.dto.SearchProductDto
import com.fitlife.app.domain.model.FoodEntry

fun ProductDto.toFoodEntry(mealId: Int): FoodEntry {
    return FoodEntry(
        mealId = mealId,
        foodName = product_name ?: "Unknown",
        calories = energy_kcal_100g ?: 0f,
        protein = proteins_100g ?: 0f,
        carbs = carbohydrates_100g ?: 0f,
        fat = fat_100g ?: 0f,
        quantity = 1f
    )
}

fun SearchProductDto.toFoodEntry(mealId: Int): FoodEntry {
    return FoodEntry(
        mealId = mealId,
        foodName = product_name ?: "Unknown",
        calories = nutriments?.energy_kcal_100g ?: 0f,
        protein = nutriments?.proteins_100g ?: 0f,
        carbs = nutriments?.carbohydrates_100g ?: 0f,
        fat = nutriments?.fat_100g ?: 0f,
        quantity = 1f
    )
}