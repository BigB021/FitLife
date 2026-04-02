package com.fitlife.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fitlife.app.domain.model.FoodEntry

@Dao
interface FoodDao {
    @Insert
    suspend fun insertFood(food: FoodEntry): Long

    @Query("SELECT * FROM FoodEntry")
    suspend fun getFood(): List<FoodEntry>
    @Query("SELECT * FROM FoodEntry WHERE id = :id")
    suspend fun getFoodById(id: Int): FoodEntry
    @Query("SELECT * FROM FoodEntry WHERE mealId = :mealId")
    suspend fun getMealFood(mealId: Int): List<FoodEntry>


    @Update
    suspend fun updateFood(food: FoodEntry)

    @Delete
    suspend fun deleteFood(food: FoodEntry)

}