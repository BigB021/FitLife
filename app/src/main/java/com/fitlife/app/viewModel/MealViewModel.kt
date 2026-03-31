package com.fitlife.app.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlife.app.data.repository.MealRepository
import com.fitlife.app.domain.model.DailySummary
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.MealWithFood
import kotlinx.coroutines.launch

class MealViewModel(private val mealRepository: MealRepository): ViewModel() {
    val mealsWithFood = MutableLiveData<List<MealWithFood>>(emptyList())
    val dailyMacros = MutableLiveData<DailySummary>()

    private fun refreshData(date: String) {
        viewModelScope.launch {
            mealsWithFood.value = mealRepository.getMealsWithFoodByDate(date)
            dailyMacros.value = mealRepository.getDailyMacros(date)
        }
    }
    fun addDailyMacros(date: String){
        viewModelScope.launch {
            mealRepository.addDailyMacros(date)
        }
    }
    fun loadDailyMacros(date: String) {
        viewModelScope.launch {
            dailyMacros.value = mealRepository.getDailyMacros(date)
        }
    }

    fun updateDailyMacros(date: String){
        viewModelScope.launch {
            mealRepository.updateDailyMacros(date)
        }
    }

    fun addMeal(meal: Meal){
        viewModelScope.launch {
            try {
                val mealId = mealRepository.addMeal(meal)
                mealRepository.updateDailyMacros(meal.date)
                refreshData(meal.date)
            } catch (e: Exception) {
                println(e)
            }
        }
    }
    fun addFood(food: FoodEntry){
        viewModelScope.launch {
            try {
                mealRepository.addFood(food)
                val meal = mealRepository.getMeal(food.mealId)
                mealRepository.updateDailyMacros(meal.date)
                refreshData(meal.date)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    // avoids multiple db calls
    fun loadMealsWithFoods() {
        viewModelScope.launch {
            mealsWithFood.value = mealRepository.getMealsWithFood()
        }
    }

    fun updateMealFood(food: FoodEntry){
        viewModelScope.launch {
            try {
                mealRepository.updateMealFood(food)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun deleteMeal(meal: Meal){
        viewModelScope.launch {
            try {
                mealRepository.deleteMeal(meal)
                mealRepository.updateDailyMacros(meal.date)
                refreshData(meal.date)
            } catch (e: Exception) {
                println(e)
            }
        }
    }
    fun deleteFood(food: FoodEntry){
        viewModelScope.launch {
            try {
                mealRepository.deleteFood(food)
                val meal = mealRepository.getMeal(food.mealId)
                mealRepository.updateDailyMacros(meal.date)
                refreshData(meal.date)
            } catch (e: Exception) {
                println(e)
            }
        }
    }


}