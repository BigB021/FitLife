package com.fitlife.app.viewModel

import androidx.lifecycle.*
import com.fitlife.app.data.repository.ExerciseRepository
import com.fitlife.app.domain.model.Exercise
import kotlinx.coroutines.launch

data class ExerciseFilter(
    val query: String = "",
    val muscle: String = "",
    val type: String = ""
)

class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    private val _localExercises = MutableLiveData<List<Exercise>>(emptyList())
    val localExercises: LiveData<List<Exercise>> = _localExercises

    private val _apiResults = MutableLiveData<List<Exercise>>(emptyList())
    val apiResults: LiveData<List<Exercise>> = _apiResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _savedResult = MutableLiveData<Exercise?>(null)
    val savedResult: LiveData<Exercise?> = _savedResult

    fun clearError() { _error.value = null }
    fun clearSavedResult() { _savedResult.value = null }
    fun clearApiResults() { _apiResults.value = emptyList() }

    fun loadLocalExercises() {
        viewModelScope.launch {
            _localExercises.value = exerciseRepository.getAllExercises()
        }
    }

    fun searchLocal(filter: ExerciseFilter) {
        viewModelScope.launch {
            _localExercises.value = when {
                filter.muscle.isNotBlank() -> exerciseRepository.searchLocalByMuscle(filter.muscle)
                filter.type.isNotBlank()   -> exerciseRepository.searchLocalByType(filter.type)
                filter.query.isNotBlank()  -> exerciseRepository.searchLocalByName(filter.query)
                else                       -> exerciseRepository.getAllExercises()
            }
        }
    }

    fun searchApi(filter: ExerciseFilter) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _apiResults.value = when {
                    filter.muscle.isNotBlank() -> exerciseRepository.searchApiByMuscle(filter.muscle)
                    filter.query.isNotBlank()  -> exerciseRepository.searchApiByName(filter.query)
                    else                       -> emptyList()
                }
            } catch (e: java.net.UnknownHostException) {
                _error.value = "No internet connection."
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveExerciseToLibrary(exercise: Exercise) {
        viewModelScope.launch {
            try {
                val saved = exerciseRepository.saveExercise(exercise)
                _savedResult.value = saved
                loadLocalExercises()
            } catch (e: Exception) {
                _error.value = "Failed to save: ${e.message}"
            }
        }
    }
}

