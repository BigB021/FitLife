package com.fitlife.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitlife.app.data.repository.MealRepository

class MealViewModelFactory(private val mealRepository: MealRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MealViewModel(mealRepository) as T
    }
}