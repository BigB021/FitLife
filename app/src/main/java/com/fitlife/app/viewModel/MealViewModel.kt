package com.fitlife.app.viewModel

import androidx.lifecycle.LiveData
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
    private val _activeMealId = MutableLiveData<Int?>(null)
    val activeMealId: LiveData<Int?> = _activeMealId

    private val _todayMealsWithFood = MutableLiveData<List<MealWithFood>>(emptyList())
    val todayMealsWithFood: LiveData<List<MealWithFood>> = _todayMealsWithFood
    private val _currentMealWithFood = MutableLiveData<MealWithFood?>(null)
    val currentMealWithFood: LiveData<MealWithFood?> = _currentMealWithFood

    fun loadCurrentMeal(mealId: Int) {
        viewModelScope.launch {
            _currentMealWithFood.value = mealRepository.getMealWithFood(mealId)
        }
    }

    // Clear on dismiss
    fun clearActiveMeal() {
        _activeMealId.value = null
        _currentMealWithFood.value = null
    }

    fun loadTodayMeals(date: String) {
        viewModelScope.launch {
            _todayMealsWithFood.value = mealRepository.getMealsWithFoodByDate(date)
        }
    }

    // Creates meal if not exists for today, exposes its real id
    fun getOrCreateMeal(type: String, date: String) {
        viewModelScope.launch {
            try {
                val existing = mealRepository.getMealByTypeAndDate(type, date)
                _activeMealId.value = existing?.id
                    ?: mealRepository.addMeal(Meal(type = type, date = date)).toInt()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun createMealAndStartLogging(
        type: String,
        date: String,
        onCreated: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val mealId = mealRepository.addMeal(
                Meal(type = type, date = date)
            ).toInt()

            onCreated(mealId)
        }
    }

    fun deleteMealIfEmpty(mealId: Int) {
        viewModelScope.launch {
            val foods = mealRepository.getMealWithFood(mealId)
            if (foods?.foods?.isEmpty() == true) {
                mealRepository.deleteMeal(foods.meal)
            }
        }
    }

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