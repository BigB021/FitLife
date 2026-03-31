package com.fitlife.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitlife.app.domain.model.DailySummary
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.MealWithFood

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: Meal): Long // meal id
    @Insert
    suspend fun insertFood(food: FoodEntry)
    @Insert
    suspend fun insertDailyMacros(dailySummary: DailySummary)

    @Query("SELECT * FROM Meal WHERE id = :id")
    suspend fun getMeal(id: Int): Meal
    @Query("SELECT * FROM DailySummary WHERE date = :date")
    suspend fun getDailyMacros(date: String): DailySummary

    @Update
    suspend fun updateMealFood(food: FoodEntry)
    @Update
    suspend fun updateDailyMacros(macros: DailySummary)

    @Delete
    suspend fun deleteMeal(meal: Meal)
    @Delete
    suspend fun deleteFood(food: FoodEntry )

    @Transaction
    @Query("SELECT * FROM Meal")
    suspend fun getMealsWithFood(): List<MealWithFood>
    @Transaction
    @Query("SELECT * FROM Meal WHERE date = :date")
    suspend fun getMealsWithFoodByDate(date: String): List<MealWithFood>


}