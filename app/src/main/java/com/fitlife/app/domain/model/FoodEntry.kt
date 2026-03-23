package com.fitlife.app.domain.model

data class FoodEntry (
    val id: Int,
    val foodName: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val quantity: Float,
    val date: String
)