package com.fitlife.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fitlife.app.domain.model.WorkoutExercise
import com.fitlife.app.domain.model.WorkoutSession


@Dao
interface WorkoutDao {
    // todo: implement workout query
    @Insert
    suspend fun insertSession(session: WorkoutSession): Long
    @Insert
    suspend fun insertExercise(exercise: WorkoutExercise)

    @Query("SELECT * FROM WorkoutSession ORDER BY date DESC")
    suspend fun getAllSessions(): List<WorkoutSession>
    @Query("SELECT * FROM WorkoutExercise WHERE sessionId= :sessionId")
    suspend fun getSessionExercises(sessionId: Int): List<WorkoutExercise>

    @Update
    suspend fun updateExercise(exercise: WorkoutExercise)

    @Delete
    suspend fun deleteExercise(exercise: WorkoutExercise)
    @Delete
    suspend fun deleteSession(session: WorkoutSession)



}