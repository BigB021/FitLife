package com.fitlife.app.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MealTypeOption(
    val type: String,
    val label: String,
    val icon: ImageVector,
    val description: String
)

val MEAL_TYPES = listOf(
    MealTypeOption(
        type        = "breakfast",
        label       = "Breakfast",
        icon        = Icons.Outlined.WbSunny,
        description = "Start your day right"
    ),
    MealTypeOption(
        type        = "lunch",
        label       = "Lunch",
        icon        = Icons.Outlined.LightMode,
        description = "Midday fuel"
    ),
    MealTypeOption(
        type        = "dinner",
        label       = "Dinner",
        icon        = Icons.Outlined.DarkMode,
        description = "Evening meal"
    ),
    MealTypeOption(
        type        = "snack",
        label       = "Snack",
        icon        = Icons.Outlined.Cookie,
        description = "Light bite between meals"
    ),
    MealTypeOption(
        type        = "pre-workout",
        label       = "Pre-Workout",
        icon        = Icons.Outlined.FitnessCenter,
        description = "Fuel before training"
    ),
    MealTypeOption(
        type        = "post-workout",
        label       = "Post-Workout",
        icon        = Icons.Outlined.SportsScore,
        description = "Recovery after training"
    ),
)
