package com.fitlife.app.domain.model

data class WorkoutExercise (
    val id: Int,
    val exerciseId: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val notes: String
)