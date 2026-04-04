package com.fitlife.app.data.repository

import com.fitlife.app.data.api.NinjasAPI
import com.fitlife.app.data.database.ExerciseDao
import com.fitlife.app.domain.model.Exercise
import com.fitlife.app.utils.toExercise

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val ninjasAPI: NinjasAPI
) {
    // Local DB functions
    suspend fun getAllExercises(): List<Exercise>          = exerciseDao.getAllExercises()
    suspend fun searchLocalByName(q: String): List<Exercise> = exerciseDao.searchByName(q)
    suspend fun searchLocalByMuscle(m: String): List<Exercise> = exerciseDao.searchByMuscle(m)
    suspend fun searchLocalByType(t: String): List<Exercise>   = exerciseDao.searchByType(t)
    suspend fun getById(id: Int): Exercise?               = exerciseDao.getById(id)

    // API search with results NOT saved automatically
    suspend fun searchApiByName(name: String): List<Exercise> = fetchFromApi(name = name)
    suspend fun searchApiByMuscle(muscle: String): List<Exercise> = fetchFromApi(muscle = muscle)

    // Save API result to local DB
    suspend fun saveExercise(exercise: Exercise): Exercise {
        val existing = exerciseDao.findByExactName(exercise.name)
        if (existing != null) return existing
        val id = exerciseDao.insertExercise(exercise).toInt()
        return exercise.copy(id = id)
    }

    private suspend fun fetchFromApi(
        name: String? = null,
        muscle: String? = null,
        type: String? = null
    ): List<Exercise> {
        val response = ninjasAPI.searchExercises(name = name, muscle = muscle, type = type)
        if (!response.isSuccessful) {
            throw Exception("Exercise search failed: HTTP ${response.code()}")
        }
        return response.body()
            ?.filter { it.name?.isNotBlank() == true }
            ?.map { it.toExercise() }
            ?: emptyList()
    }
}