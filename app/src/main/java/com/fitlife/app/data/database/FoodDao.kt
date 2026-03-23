package com.fitlife.app.data.database

import androidx.room.Dao
import androidx.room.Query
import com.fitlife.app.domain.model.FoodEntry

@Dao
interface FoodDao {
    // todo: correctly implement food query
    @Query("SELECT * FROM FoodEntry WHERE foodName = foodName")
    suspend fun getFood(foodName: String): FoodEntry?

}