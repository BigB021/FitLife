package com.fitlife.app.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlife.app.data.repository.MealRepository
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.MealWithFood
import kotlinx.coroutines.launch

class MealViewModel(private val mealRepository: MealRepository): ViewModel() {
    val meals = MutableLiveData<List<Meal>>(emptyList())
    val foodList = MutableLiveData<List<FoodEntry>>(emptyList())
    val mealsWithFood = MutableLiveData<List<MealWithFood>>(emptyList())


    fun addMeal(meal: Meal){
        viewModelScope.launch {
            try {
                val mealId = mealRepository.addMeal(meal)
                getAllMeals()
            } catch (e: Exception) {
                println(e)
            }
        }
    }
    fun addFood(food: FoodEntry){
        viewModelScope.launch {
            try {
                mealRepository.addFood(food)
                getAllMeals()
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

    fun getAllMeals(){
        viewModelScope.launch {
            try {
                meals.value = mealRepository.getAllMeals()
            } catch (e: Exception) {
                println(e)
            }
        }
    }
    fun getMealFood(mealId: Int){
        viewModelScope.launch {
            try {
                foodList.value = mealRepository.getMealFood(mealId)
            } catch (e: Exception) {
                println(e)
            }
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
                meals.value = mealRepository.getAllMeals()
            } catch (e: Exception) {
                println(e)
            }
        }
    }
    fun deleteFood(food: FoodEntry){
        viewModelScope.launch {
            try {
                mealRepository.deleteFood(food)
                getAllMeals()
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}