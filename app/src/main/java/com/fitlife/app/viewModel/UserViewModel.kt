package com.fitlife.app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitlife.app.data.repository.UserRepository
import com.fitlife.app.domain.model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class UserFormState(
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val activityLevel: String = "",
    val goalType: String = ""
)
class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    // Form state for UI
    private val _formState = MutableLiveData(UserFormState())
    val formState: LiveData<UserFormState> = _formState
    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult
    // Saved user
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // Form update functions

    fun onNameChange(name: String) {
        _formState.value = _formState.value?.copy(name = name)
    }

    fun onAgeChange(age: String) {
        _formState.value = _formState.value?.copy(age = age)
    }

    fun onGenderChange(gender: String) {
        _formState.value = _formState.value?.copy(gender = gender)
    }

    fun onHeightChange(height: String) {
        _formState.value = _formState.value?.copy(height = height)
    }

    fun onWeightChange(weight: String) {
        _formState.value = _formState.value?.copy(weight = weight)
    }

    fun onActivityLevelChange(level: String) {
        _formState.value = _formState.value?.copy(activityLevel = level)
    }

    fun onGoalChange(goal: String) {
        _formState.value = _formState.value?.copy(goalType = goal)
    }

    // Save user
    fun saveUser() {
        viewModelScope.launch {
            val state = _formState.value ?: return@launch
            try {

                val draft = User(
                    name = state.name,
                    age = state.age.toIntOrNull() ?: 0,
                    gender = state.gender,
                    height = state.height.toFloatOrNull() ?: 0f,
                    weight = state.weight.toFloatOrNull() ?: 0f,
                    activityLevel = state.activityLevel,
                    goalType = state.goalType,
                    calorieTarget = 0f, proteinTarget = 0f,
                    carbTarget = 0f,    fatTarget = 0f
                )

                // Calculate and apply macros
                val macros = userRepository.calculateTargetMacros(draft)
                val user = draft.copy(
                    calorieTarget = macros.calories,
                    proteinTarget = macros.protein,
                    carbTarget    = macros.carbs,
                    fatTarget     = macros.fat
                )

                userRepository.addUser(user)
                _user.value = user
                _saveResult.value = Result.success(Unit)

            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getUser()
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch {
            userRepository.updateUser(user)
            _user.value = user
        }
    }

    fun deleteUser(user: User){
        viewModelScope.launch {
            userRepository.deleteUser(user)
            _user.value = null
        }
    }

}