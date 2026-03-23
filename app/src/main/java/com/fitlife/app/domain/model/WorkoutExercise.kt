package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class WorkoutExercise (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val notes: String
)