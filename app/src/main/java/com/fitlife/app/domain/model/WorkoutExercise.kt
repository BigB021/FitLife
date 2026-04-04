package com.fitlife.app.domain.model

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutExercise (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val exerciseId: Int,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val notes: String
)