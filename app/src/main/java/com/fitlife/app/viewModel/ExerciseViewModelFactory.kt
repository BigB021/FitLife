package com.fitlife.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitlife.app.data.repository.ExerciseRepository
import com.fitlife.app.data.repository.WorkoutRepository

class ExerciseViewModelFactory(private val exerciseRepository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExerciseViewModel(exerciseRepository) as T
    }
}