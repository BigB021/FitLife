package com.fitlife.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fitlife.app.ui.components.CalorieSummaryCard
import com.fitlife.app.ui.components.GreetingHeader
import com.fitlife.app.ui.components.MacroProgressCard
import com.fitlife.app.ui.components.QuickActionsRow
import com.fitlife.app.ui.components.RecentWorkoutsCard
import com.fitlife.app.utils.todayAsString
import com.fitlife.app.viewModel.MealViewModel
import com.fitlife.app.viewModel.UserViewModel
import com.fitlife.app.viewModel.WorkoutViewModel

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    mealViewModel: MealViewModel,
    workoutViewModel: WorkoutViewModel,
    onNavigateToMeals: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {}
) {
    val user by userViewModel.user.observeAsState()
    val dailyMacros by mealViewModel.dailyMacros.observeAsState()
    val sessions by workoutViewModel.sessions.observeAsState(emptyList())
    val today = todayAsString()

    LaunchedEffect(Unit) {
        mealViewModel.loadDailyMacros(today)
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

        // Greeting
        GreetingHeader(user = user)

        // Calorie summary ring
        user?.let { u ->
            CalorieSummaryCard(user = u, summary = dailyMacros)
        }

        // Macro progress bars
        user?.let { u ->
            MacroProgressCard(user = u, summary = dailyMacros)
        }

        // Quick actions
        QuickActionsRow(
            onAddMeal = onNavigateToMeals,
            onAddWorkout = onNavigateToWorkout
        )

        // Recent workouts
        if (sessions.isNotEmpty()) {
            RecentWorkoutsCard(sessions = sessions.take(3))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
