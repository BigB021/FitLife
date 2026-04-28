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
){
    val totalCalories: Int get() = foods.sumOf { it.calories * it.quantity / 100.0 }.toInt()
    val totalProtein: Int  get() = foods.sumOf { it.protein  * it.quantity / 100.0 }.toInt()
    val totalCarbs: Int    get() = foods.sumOf { it.carbs    * it.quantity / 100.0 }.toInt()
    val totalFat: Int      get() = foods.sumOf { it.fat      * it.quantity / 100.0 }.toInt()
}