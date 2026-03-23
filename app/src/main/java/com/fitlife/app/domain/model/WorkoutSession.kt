package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class WorkoutSession (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val muscleGroup: String
)