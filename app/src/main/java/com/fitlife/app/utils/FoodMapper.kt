package com.fitlife.app.utils

import com.fitlife.app.data.dto.ProductDto
import com.fitlife.app.data.dto.SearchProductDto
import com.fitlife.app.data.dto.UsdaFoodDto
import com.fitlife.app.domain.model.FoodEntry

fun ProductDto.toFoodEntry(mealId: Int): FoodEntry = FoodEntry(
    mealId   = mealId,
    foodName = product_name?.takeIf { it.isNotBlank() } ?: "Unknown",
    calories = nutriments?.resolvedKcal() ?: 0f,
    protein  = nutriments?.resolvedProtein() ?: 0f,
    carbs    = nutriments?.resolvedCarbs() ?: 0f,
    fat      = nutriments?.resolvedFat() ?: 0f,
    quantity = 100f
)

fun SearchProductDto.toFoodEntry(mealId: Int): FoodEntry = FoodEntry(
    mealId   = mealId,
    foodName = productName?.takeIf { it.isNotBlank() } ?: "Unknown",
    calories = nutriments?.resolvedKcal() ?: 0f,
    protein  = nutriments?.resolvedProtein() ?: 0f,
    carbs    = nutriments?.resolvedCarbs() ?: 0f,
    fat      = nutriments?.resolvedFat() ?: 0f,
    quantity = 100f
)

fun UsdaFoodDto.toFoodEntry(mealId: Int): FoodEntry {
    // USDA descriptions are often "APPLE, RAW" or "Chicken, broilers..."
    // Normalize to title case and strip after comma for branded items
    val cleanName = description
        ?.split(",")
        ?.first()
        ?.trim()
        ?.lowercase()
        ?.replaceFirstChar { it.uppercase() }
        ?: "Unknown"

    return FoodEntry(
        mealId   = mealId,
        foodName = cleanName,
        calories = calories(),
        protein  = protein(),
        carbs    = carbs(),
        fat      = fat(),
        quantity = 100f
    )
}