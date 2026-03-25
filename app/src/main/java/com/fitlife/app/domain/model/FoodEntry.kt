package com.fitlife.app.domain.model

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["mealId"])]
)
data class FoodEntry (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealId: Int,
    val foodName: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val quantity: Float
)