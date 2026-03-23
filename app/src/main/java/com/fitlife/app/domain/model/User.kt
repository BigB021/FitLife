package com.fitlife.app.domain.model

import androidx.room.PrimaryKey

data class User(
    val id: Int,
    val name: String,
    val age: Int,
    val gender: String,
    val height: Float,
    val weight: Float,
    val goalType: String,
    val activityLevel: String,
    val calorieTarget: Float,
    val proteinTarget: Float,
    val carbTarget: Float,
    val fatTarget: Float
)