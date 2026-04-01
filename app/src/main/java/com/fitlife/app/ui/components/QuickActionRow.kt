package com.fitlife.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickActionsRow(
    onAddMeal: () -> Unit,
    onAddWorkout: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            modifier = Modifier.weight(1f),
            label = "Log Meal",
            icon = Icons.Outlined.Restaurant,
            onClick = onAddMeal
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            label = "Log Workout",
            icon = Icons.Outlined.FitnessCenter,
            onClick = onAddWorkout
        )
    }
}