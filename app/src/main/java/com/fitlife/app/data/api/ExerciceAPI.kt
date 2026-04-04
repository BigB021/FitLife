package com.fitlife.app.data.api

import com.fitlife.app.data.dto.ExerciseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NinjasAPI{
    @GET("v1/exercises")
    suspend fun searchExercises(
        @Query("name")      name: String?      = null,
        @Query("muscle")    muscle: String?    = null,
        @Query("type")      type: String?      = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("equipment") equipment: String? = null,
        @Query("limit")     limit: Int         = 20,
        @Query("offset")    offset: Int        = 0
    ): Response<List<ExerciseDto>>

}