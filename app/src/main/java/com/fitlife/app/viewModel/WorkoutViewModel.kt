package com.fitlife.app.viewModel

import androidx.lifecycle.*
import com.fitlife.app.data.repository.WorkoutRepository
import com.fitlife.app.domain.model.WorkoutExercise
import com.fitlife.app.domain.model.WorkoutSession
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    // ── All sessions (history) ─────────────────────────────────────────────
    val sessions = MutableLiveData<List<WorkoutSession>>(emptyList())
    val exercises = MutableLiveData<List<WorkoutExercise>>(emptyList())

    // ── Active session state ───────────────────────────────────────────────
    private val _activeSession = MutableLiveData<WorkoutSession?>(null)
    val activeSession: LiveData<WorkoutSession?> = _activeSession

    private val _sessionExercises = MutableLiveData<List<WorkoutExercise>>(emptyList())
    val sessionExercises: LiveData<List<WorkoutExercise>> = _sessionExercises

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun clearError() { _error.value = null }

    // ── History ────────────────────────────────────────────────────────────
    fun getAllSessions() {
        viewModelScope.launch {
            sessions.value = workoutRepository.getAllSessions()
        }
    }

    fun getSessionExercises(sessionId: Int) {
        viewModelScope.launch {
            exercises.value = workoutRepository.getSessionExercises(sessionId)
        }
    }

    fun updateExercise(exercise: WorkoutExercise) {
        viewModelScope.launch { workoutRepository.updateExercise(exercise) }
    }

    fun deleteExercise(exercise: WorkoutExercise) {
        viewModelScope.launch { workoutRepository.deleteWorkoutExercise(exercise) }
    }

    // ── Active session lifecycle ───────────────────────────────────────────
    fun startSession(muscleGroup: String, date: String) {
        viewModelScope.launch {
            try {
                val session = WorkoutSession(date = date, muscleGroup = muscleGroup)
                val id = workoutRepository.addSession(session).toInt()
                _activeSession.value = session.copy(id = id)
                _sessionExercises.value = emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to start session: ${e.message}"
            }
        }
    }

    fun logExercise(exerciseId: Int, sets: Int, reps: Int, weight: Float, notes: String = "") {
        val sessionId = _activeSession.value?.id ?: return
        viewModelScope.launch {
            try {
                workoutRepository.addWorkoutExercise(
                    WorkoutExercise(
                        sessionId  = sessionId,
                        exerciseId = exerciseId,
                        sets       = sets,
                        reps       = reps,
                        weight     = weight,
                        notes      = notes
                    )
                )
                refreshSessionExercises()
            } catch (e: Exception) {
                _error.value = "Failed to log: ${e.message}"
            }
        }
    }

    fun removeExerciseFromSession(workoutExercise: WorkoutExercise) {
        viewModelScope.launch {
            workoutRepository.deleteWorkoutExercise(workoutExercise)
            refreshSessionExercises()
        }
    }

    fun finishSession() {
        _activeSession.value = null
        _sessionExercises.value = emptyList()
        getAllSessions()   // refresh history
    }

    fun discardSession() {
        viewModelScope.launch {
            _activeSession.value?.let { workoutRepository.deleteSession(it) }
            _activeSession.value = null
            _sessionExercises.value = emptyList()
        }
    }

    private fun refreshSessionExercises() {
        val sessionId = _activeSession.value?.id ?: return
        viewModelScope.launch {
            _sessionExercises.value = workoutRepository.getSessionExercises(sessionId)
        }
    }
}