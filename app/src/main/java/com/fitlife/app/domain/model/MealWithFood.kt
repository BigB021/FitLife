package com.fitlife.app.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithFood(

    @Embedded val meal: Meal,

    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val foods: List<FoodEntry>
)