package com.fitlife.app.domain.model

import androidx.room.Entity

@Entity()
data class DailySummary (
    val date: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
)