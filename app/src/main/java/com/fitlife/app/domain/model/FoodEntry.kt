package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class FoodEntry (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodName: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val quantity: Float,
    val date: String
)