package com.fitlife.app.domain.model

import java.sql.Date

data class WorkoutSession (
    val id: Int,
    val date: String,
    val muscleGroup: String
)