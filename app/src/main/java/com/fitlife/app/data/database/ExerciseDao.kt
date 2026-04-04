package com.fitlife.app.data.database

import androidx.room.*
import com.fitlife.app.domain.model.Exercise

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Query("SELECT * FROM Exercise ORDER BY name ASC")
    suspend fun getAllExercises(): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE name LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE muscle = :muscle")
    suspend fun searchByMuscle(muscle: String): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE type = :type")
    suspend fun searchByType(type: String): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE name = :name LIMIT 1")
    suspend fun findByExactName(name: String): Exercise?

    @Query("SELECT * FROM Exercise WHERE id = :id")
    suspend fun getById(id: Int): Exercise?
}