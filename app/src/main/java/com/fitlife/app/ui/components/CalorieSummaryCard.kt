package com.fitlife.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitlife.app.domain.model.DailySummary
import com.fitlife.app.domain.model.User


@Composable
fun CalorieSummaryCard(user: User, summary: DailySummary?) {
    val consumed = summary?.totalCalories ?: 0f
    val target = user.calorieTarget
    val remaining = (target - consumed).coerceAtLeast(0f)
    val progress = if (target > 0) (consumed / target).coerceIn(0f, 1f) else 0f
    val isOver = consumed > target

    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ring chart
            CalorieRing(
                progress = progress,
                consumed = consumed,
                target = target,
                isOver = isOver,
                size = 120.dp
            )

            // Stats column
            Column(
                modifier = Modifier.weight(1f).padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CalorieStatItem(
                    label = "Target",
                    value = "${target.toInt()} kcal",
                    icon = Icons.Outlined.Flag,
                    tint = MaterialTheme.colorScheme.primary
                )
                CalorieStatItem(
                    label = "Consumed",
                    value = "${consumed.toInt()} kcal",
                    icon = Icons.Outlined.LocalFireDepartment,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                CalorieStatItem(
                    label = if (isOver) "Over by" else "Remaining",
                    value = "${(consumed - target).let { if (isOver) it else remaining }.toInt()} kcal",
                    icon = if (isOver) Icons.Outlined.Warning else Icons.Outlined.CheckCircle,
                    tint = if (isOver) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}