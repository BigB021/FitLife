package com.fitlife.app.data.repository

import com.fitlife.app.data.database.MealDao
import com.fitlife.app.domain.model.DailySummary
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.MealWithFood

class MealRepository(private val mealDao: MealDao) {
    suspend fun addMeal(meal: Meal):Long = mealDao.insertMeal(meal)
    suspend fun addFood(food: FoodEntry) = mealDao.insertFood(food)
    suspend fun addDailyMacros(date: String) = mealDao.insertDailyMacros(DailySummary(date,.0f,.0f,.0f,.0f))
    suspend fun getAllMeals(): List<Meal> = mealDao.getAllMeals()
    suspend fun getMeal(id: Int): Meal = mealDao.getMeal(id)
    suspend fun getMealFood(mealId: Int): List<FoodEntry> = mealDao.getMealFood(mealId)
    suspend fun getMealsByDate(date: String): List<Meal> = mealDao.getMealsByDate(date)
    suspend fun getDailyMacros(date: String): DailySummary {
        return try {
            mealDao.getDailyMacros(date)
        } catch (e: Exception) {
            val summary = DailySummary(date, 0f, 0f, 0f, 0f)
            mealDao.insertDailyMacros(summary)
            summary
        }
    }
    suspend fun updateMealFood(food: FoodEntry) = mealDao.updateMealFood(food)
    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)
    suspend fun deleteFood(food: FoodEntry) = mealDao.deleteFood(food)


    suspend fun getMealsWithFood(): List<MealWithFood> = mealDao.getMealsWithFood()

    suspend fun updateDailyMacros(date: String) {
        val todayMeals = mealDao.getMealsWithFoodByDate(date)

        val calories = todayMeals.sumOf { meal ->
            meal.foods.sumOf { (it.calories * it.quantity).toDouble() }
        }.toFloat()
        val protein = todayMeals.sumOf { meal ->
            meal.foods.sumOf { (it.protein * it.quantity).toDouble() }
        }.toFloat()
        val carbs = todayMeals.sumOf { meal ->
            meal.foods.sumOf { (it.carbs * it.quantity).toDouble() }
        }.toFloat()
        val fat = todayMeals.sumOf { meal ->
            meal.foods.sumOf { (it.fat * it.quantity).toDouble() }
        }.toFloat()
        mealDao.updateDailyMacros(
            DailySummary(date, calories, protein, carbs, fat)
        )
    }


}