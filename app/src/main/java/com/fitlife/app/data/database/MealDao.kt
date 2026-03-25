package com.fitlife.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.MealWithFood

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: Meal): Long // meal id
    @Insert
    suspend fun insertFood(food: FoodEntry)

    @Query("SELECT * FROM Meal")
    suspend fun getAllMeals(): List<Meal>
    @Query("SELECT * FROM FoodEntry WHERE mealId = :mealId")
    suspend fun getMealFood(mealId: Int): List<FoodEntry>

    @Update
    suspend fun updateMealFood(food: FoodEntry)

    @Delete
    suspend fun deleteMeal(meal: Meal)
    @Delete
    suspend fun deleteFood(food: FoodEntry )

    @Transaction
    @Query("SELECT * FROM Meal")
    suspend fun getMealsWithFood(): List<MealWithFood>


}