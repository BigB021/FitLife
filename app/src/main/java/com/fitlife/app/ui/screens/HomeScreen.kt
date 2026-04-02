package com.fitlife.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.MealWithFood
import com.fitlife.app.ui.components.CalorieSummaryCard
import com.fitlife.app.ui.components.GreetingHeader
import com.fitlife.app.ui.components.MacroProgressCard
import com.fitlife.app.ui.components.QuickActionsRow
import com.fitlife.app.ui.components.RecentWorkoutsCard
import com.fitlife.app.ui.components.SectionCard
import com.fitlife.app.utils.todayAsString
import com.fitlife.app.viewModel.MealViewModel
import com.fitlife.app.viewModel.UserViewModel
import com.fitlife.app.viewModel.WorkoutViewModel

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    mealViewModel: MealViewModel,
    workoutViewModel: WorkoutViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToMeals: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {}
) {
    val user by userViewModel.user.observeAsState()
    val dailyMacros by mealViewModel.dailyMacros.observeAsState()
    val todayMealsWithFood by mealViewModel.todayMealsWithFood.observeAsState(emptyList())
    val sessions by workoutViewModel.sessions.observeAsState(emptyList())
    val today = todayAsString()

    LaunchedEffect(Unit) {
        mealViewModel.loadDailyMacros(today)
        mealViewModel.loadTodayMeals(today)
        workoutViewModel.getAllSessions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        GreetingHeader(user = user)
        user?.let { CalorieSummaryCard(user = it, summary = dailyMacros) }
        user?.let { MacroProgressCard(user = it, summary = dailyMacros) }
        QuickActionsRow(onAddMeal = onNavigateToMeals, onAddWorkout = onNavigateToWorkout)

        // Today's meals with their foods
        if (todayMealsWithFood.isNotEmpty()) {
            TodayMealsCard(
                mealsWithFood = todayMealsWithFood,
                onDeleteFood  = { food -> mealViewModel.deleteFood(food) }
            )
        }

        if (sessions.isNotEmpty()) RecentWorkoutsCard(sessions = sessions.take(3))
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Meal History")
        }
    }
}

@Composable
fun TodayMealsCard(
    mealsWithFood: List<MealWithFood>,
    onDeleteFood: (FoodEntry) -> Unit
) {
    SectionCard {
        Text(
            text = "Today's Meals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        mealsWithFood.forEachIndexed { index, mealWithFood ->
            if (mealWithFood.foods.isNotEmpty()) {
                MealWithFoodRow(mealWithFood = mealWithFood, onDeleteFood = onDeleteFood)
                if (index < mealsWithFood.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

// Single Meal + its food entries
@Composable
fun MealWithFoodRow(
    mealWithFood: MealWithFood,
    onDeleteFood: (FoodEntry) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column {
        // Meal header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = mealTypeIcon(mealWithFood.meal.type),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = mealWithFood.meal.type.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Total calories for this meal
                val mealCalories = mealWithFood.foods.sumOf {
                    (it.calories * it.quantity / 100.0)
                }.toInt()
                Text(
                    text = "$mealCalories kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Food entries
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 6.dp)) {
                mealWithFood.foods.forEach { food ->
                    FoodEntryRow(food = food, onDelete = { onDeleteFood(food) })
                }
            }
        }
    }
}

// Single food entry row
@Composable
fun FoodEntryRow(food: FoodEntry, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(food.foodName, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${food.quantity.toInt()}g · " +
                        "P ${(food.protein * food.quantity / 100).toInt()}g  " +
                        "C ${(food.carbs * food.quantity / 100).toInt()}g  " +
                        "F ${(food.fat * food.quantity / 100).toInt()}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Outlined.DeleteOutline,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Meal type icon helper
fun mealTypeIcon(type: String): ImageVector = when (type.lowercase()) {
    "breakfast" -> Icons.Outlined.WbSunny
    "lunch"     -> Icons.Outlined.LightMode
    "dinner"    -> Icons.Outlined.DarkMode
    "snack"     -> Icons.Outlined.Cookie
    else        -> Icons.Outlined.Restaurant
}