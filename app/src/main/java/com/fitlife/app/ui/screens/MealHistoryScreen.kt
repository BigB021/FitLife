package com.fitlife.app.ui.screens

import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.MealWithFood
import com.fitlife.app.viewModel.MealViewModel

@Composable
fun MealHistoryList(
    meals: List<MealWithFood>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        items(meals, key = { it.meal.id }) { mealWithFood ->
            MealCard(mealWithFood)
        }
    }
}

@Composable
fun MealCard(mealWithFood: MealWithFood) {

    val meal = mealWithFood.meal
    val foods = mealWithFood.foods

    // 🔢 Calculate totals
    val totalCalories = foods.sumOf { (it.calories * it.quantity).toDouble() }.toInt()
    val totalProtein = foods.sumOf { (it.protein * it.quantity).toDouble() }.toInt()
    val totalCarbs = foods.sumOf { (it.carbs * it.quantity).toDouble() }.toInt()
    val totalFat = foods.sumOf { (it.fat * it.quantity).toDouble() }.toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 🧾 Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = meal.type,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = meal.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🍽️ Food list
            foods.forEach { food ->
                FoodRow(food)
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(6.dp))

            // 🔢 Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold)

                Text("$totalCalories kcal", fontWeight = FontWeight.Bold)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroChip("P", "${totalProtein}g")
                MacroChip("C", "${totalCarbs}g")
                MacroChip("F", "${totalFat}g")
            }
        }
    }
}

@Composable
fun FoodRow(food: FoodEntry) {

    val quantity = food.quantity
    val calories = (food.calories * quantity).toInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.foodName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${quantity.toInt()}g",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$calories kcal",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MacroChip(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MealHistoryScreen(mealViewModel: MealViewModel, onBack: () -> Unit) {

    val meals by mealViewModel.mealsWithFood.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        mealViewModel.loadMealsWithFoods()
    }


    MealHistoryList(meals)
}