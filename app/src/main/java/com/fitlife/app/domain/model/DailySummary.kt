package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class DailySummary (
    @PrimaryKey val date: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
)