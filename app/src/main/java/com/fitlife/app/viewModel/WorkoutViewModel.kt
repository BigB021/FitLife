package com.fitlife.app.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlife.app.data.repository.WorkoutRepository
import com.fitlife.app.domain.model.WorkoutExercise
import com.fitlife.app.domain.model.WorkoutSession
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutRepository: WorkoutRepository): ViewModel() {
    val sessions = MutableLiveData<List<WorkoutSession>>(emptyList())
    val exercises = MutableLiveData<List<WorkoutExercise>>(emptyList())

    fun addSession(session: WorkoutSession){
        viewModelScope.launch {
            val sessionId = workoutRepository.addSession(session)
        }
    }
    fun addExercise(exercise: WorkoutExercise){
        viewModelScope.launch {
            workoutRepository.addExercise(exercise)
        }
    }

    fun getAllSessions(){
        viewModelScope.launch {
             sessions.value = workoutRepository.getAllSessions()
        }
    }
    fun getSessionExercises(sessionId: Int){
        viewModelScope.launch {
            exercises.value = workoutRepository.getSessionExercises(sessionId)
        }
    }

    fun updateExercise(exercise: WorkoutExercise){
        viewModelScope.launch {
            workoutRepository.updateExercise(exercise)
        }
    }
    fun deleteExercise(exercise: WorkoutExercise){
        viewModelScope.launch {
            workoutRepository.deleteExercise(exercise)
        }
    }
}