package com.fitlife.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitlife.app.data.repository.FoodRepository

class FoodViewModelFactory(private val foodRepository: FoodRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FoodViewModel(foodRepository) as T
    }
}