package com.fitlife.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitlife.app.domain.model.DailySummary
import com.fitlife.app.domain.model.User


@Composable
fun MacroProgressCard(user: User, summary: DailySummary?) {
    SectionCard {
        Text(
            text = "Macros Today",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MacroProgressRow(
                label = "Protein",
                consumed = summary?.totalProtein ?: 0f,
                target = user.proteinTarget,
                color = MaterialTheme.colorScheme.primary
            )
            MacroProgressRow(
                label = "Carbs",
                consumed = summary?.totalCarbs ?: 0f,
                target = user.carbTarget,
                color = MaterialTheme.colorScheme.tertiary
            )
            MacroProgressRow(
                label = "Fat",
                consumed = summary?.totalFat ?: 0f,
                target = user.fatTarget,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}