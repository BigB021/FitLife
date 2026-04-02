package com.fitlife.app.data.dto

data class UsdaFoodDto(
    val fdcId: Int?,
    val description: String?,
    val dataType: String?,
    val brandOwner: String?,
    val servingSize: Double?,
    val servingSizeUnit: String?,
    val foodNutrients: List<UsdaNutrientDto>?
) {
    // IDs confirmed from your sample response
    private fun nutrientValue(id: Int): Float =
        foodNutrients
            ?.firstOrNull { it.nutrientId == id }
            ?.value
            ?.toFloat() ?: 0f

    fun calories(): Float = nutrientValue(1008)  // "Energy" KCAL
    fun protein(): Float  = nutrientValue(1003)  // "Protein"
    fun carbs(): Float    = nutrientValue(1005)  // "Carbohydrate, by difference"
    fun fat(): Float      = nutrientValue(1004)  // "Total lipid (fat)"
}