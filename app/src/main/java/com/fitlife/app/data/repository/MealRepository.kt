package com.fitlife.app.data.repository

import com.fitlife.app.data.database.MealDao
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.MealWithFood

class MealRepository(private val mealDao: MealDao) {
    suspend fun addMeal(meal: Meal):Long = mealDao.insertMeal(meal)
    suspend fun addFood(food: FoodEntry) = mealDao.insertFood(food)
    suspend fun getAllMeals(): List<Meal> = mealDao.getAllMeals()
    suspend fun getMealFood(mealId: Int): List<FoodEntry> = mealDao.getMealFood(mealId)
    suspend fun updateMealFood(food: FoodEntry) = mealDao.updateMealFood(food)
    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)
    suspend fun deleteFood(food: FoodEntry) = mealDao.deleteFood(food)

    suspend fun getMealsWithFood(): List<MealWithFood> = mealDao.getMealsWithFood()
    // todo: total calories per meal, macro sums and daily totals
}