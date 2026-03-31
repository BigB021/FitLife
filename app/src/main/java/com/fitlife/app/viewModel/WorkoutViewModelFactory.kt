package com.fitlife.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitlife.app.data.repository.WorkoutRepository

class WorkoutViewModelFactory(private val workoutRepository: WorkoutRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutViewModel(workoutRepository) as T
    }
}