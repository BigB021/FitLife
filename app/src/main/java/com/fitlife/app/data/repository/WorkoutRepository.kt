package com.fitlife.app.data.repository

import com.fitlife.app.data.database.WorkoutDao
import com.fitlife.app.domain.model.WorkoutExercise
import com.fitlife.app.domain.model.WorkoutSession

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    suspend fun addSession(session: WorkoutSession): Long = workoutDao.insertSession(session)
    suspend fun addWorkoutExercise(exercise: WorkoutExercise) = workoutDao.insertExercise(exercise)
    suspend fun getAllSessions(): List<WorkoutSession> = workoutDao.getAllSessions()
    suspend fun getSessionExercises(sessionId: Int): List<WorkoutExercise> = workoutDao.getSessionExercises(sessionId)
    suspend fun updateExercise(exercise: WorkoutExercise) = workoutDao.updateExercise(exercise)
    suspend fun deleteWorkoutExercise(exercise: WorkoutExercise) = workoutDao.deleteExercise(exercise)
    suspend fun deleteSession(session: WorkoutSession) = workoutDao.deleteSession(session)

}