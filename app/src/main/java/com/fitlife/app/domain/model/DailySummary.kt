package com.fitlife.app.domain.model

data class DailySummary (
    val date: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
)