package com.fitlife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitlife.app.data.api.OpenFoodAPI
import com.fitlife.app.data.api.UsdaFoodAPI
import com.fitlife.app.data.database.AppDatabase
import com.fitlife.app.data.repository.*
import com.fitlife.app.ui.screens.*
import com.fitlife.app.utils.todayAsString
import com.fitlife.app.viewModel.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository
    private lateinit var mealRepository: MealRepository
    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var foodRepository: FoodRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(this)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "FitLife/1.0 (Android)")
                        .build()
                )
            }
            .build()

        val openFoodApi = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodAPI::class.java)

        val usdaApi = Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsdaFoodAPI::class.java)

        val USDA_API_KEY = "thIkdfBjMfBQ9UtDaLHXha6BVbT00DAwbzbxwZxX"

        foodRepository = FoodRepository(db.FoodDao(), openFoodApi, usdaApi, USDA_API_KEY)
        userRepository     = UserRepository(db.userDao())
        mealRepository     = MealRepository(db.mealDao())
        workoutRepository  = WorkoutRepository(db.WorkoutDao())

        setContent {
            val userViewModel: UserViewModel       = viewModel(factory = UserViewModelFactory(userRepository))
            val mealViewModel: MealViewModel       = viewModel(factory = MealViewModelFactory(mealRepository))
            val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(workoutRepository))
            val foodViewModel: FoodViewModel       = viewModel(factory = FoodViewModelFactory(foodRepository))

            val user by userViewModel.user.observeAsState()

            // UI state
            var isLoading by remember { mutableStateOf(true) }
            var showFoodSearch by remember { mutableStateOf(false) }
            var activeMealId by remember { mutableIntStateOf(0) }
            var activeDate by remember { mutableStateOf(todayAsString()) }
            var currentScreen by remember { mutableStateOf<Screen?>(null) }

            LaunchedEffect(user) {
                currentScreen = if (user == null) Screen.SETUP else Screen.HOME
            }

            when (currentScreen) {

                null -> SplashScreen()

                Screen.SETUP -> {
                    SetupScreen(userViewModel)
                }

                Screen.HOME -> {
                    HomeScreen(
                        userViewModel = userViewModel,
                        mealViewModel = mealViewModel,
                        workoutViewModel = workoutViewModel,

                        onNavigateToMeals = {
                            val today = todayAsString()

                            mealViewModel.createMealAndStartLogging(
                                type = "Meal",
                                date = today
                            ) { mealId ->
                                activeMealId = mealId
                                activeDate = today
                                currentScreen = Screen.FOOD_SEARCH
                            }
                        },

                        onNavigateToHistory = {
                            currentScreen = Screen.MEAL_HISTORY
                        },

                        onNavigateToWorkout = {
                            // later
                        }
                    )
                }

                Screen.FOOD_SEARCH -> {

                    MealLoggingScreen(
                        foodViewModel = foodViewModel,
                        mealViewModel = mealViewModel,
                        targetDate = activeDate,
                        onDismiss = {
                            mealViewModel.deleteMealIfEmpty(activeMealId)
                            currentScreen = Screen.HOME
                        }
                    )
                }

                Screen.MEAL_HISTORY -> {
                    MealHistoryScreen(
                        mealViewModel = mealViewModel,
                        onBack = {
                            currentScreen = Screen.HOME
                        }
                    )
                }
            }
        }
    }
}

enum class Screen {
    SETUP,
    HOME,
    FOOD_SEARCH,
    MEAL_HISTORY
}
// Splash / Loading Screen
@Composable
fun SplashScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}
