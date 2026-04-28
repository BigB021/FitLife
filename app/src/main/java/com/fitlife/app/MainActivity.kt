package com.fitlife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitlife.app.data.api.NinjasAPI
import com.fitlife.app.data.api.OpenFoodAPI
import com.fitlife.app.data.api.UsdaFoodAPI
import com.fitlife.app.data.database.AppDatabase
import com.fitlife.app.data.repository.*
import com.fitlife.app.ui.screens.*
import com.fitlife.app.ui.theme.FitLifeTheme
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
    private lateinit var exerciseRepository: ExerciseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(this)

        val USDA_API_KEY = "thIkdfBjMfBQ9UtDaLHXha6BVbT00DAwbzbxwZxX"
        val NINJAS_API_KEY = "3TN7xKw8Gv0Y68TREG2JsxJfs2c4apwFqtKSnDIX"

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

        val ninjasClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("X-Api-Key", NINJAS_API_KEY)
                        .build()
                )
            }
            .build()

        val ninjasApi = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .client(ninjasClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NinjasAPI::class.java)

        foodRepository = FoodRepository(db.FoodDao(), openFoodApi, usdaApi, USDA_API_KEY)
        userRepository     = UserRepository(db.userDao())
        mealRepository     = MealRepository(db.mealDao())
        workoutRepository  = WorkoutRepository(db.WorkoutDao())
        exerciseRepository = ExerciseRepository(db.ExerciseDao(), ninjasApi)


        setContent {
            FitLifeTheme {
                val userViewModel: UserViewModel       = viewModel(factory = UserViewModelFactory(userRepository))
                val mealViewModel: MealViewModel       = viewModel(factory = MealViewModelFactory(mealRepository))
                val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(workoutRepository))
                val foodViewModel: FoodViewModel       = viewModel(factory = FoodViewModelFactory(foodRepository))
                val exerciseViewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(exerciseRepository))


                // UI state
                var isLoading by remember { mutableStateOf(true) }
                var showFoodSearch by remember { mutableStateOf(false) }
                var activeMealId by remember { mutableIntStateOf(0) }
                var activeDate by remember { mutableStateOf(todayAsString()) }
                var currentScreen by remember { mutableStateOf<Screen?>(null) }
                var userLoaded by remember { mutableStateOf(false) }
                val user by userViewModel.user.observeAsState()

                LaunchedEffect(Unit) {
                    userViewModel.loadUser()
                    userLoaded = true
                }

                LaunchedEffect(userLoaded, user) {
                    if (!userLoaded) return@LaunchedEffect
                    // Only set initial screen - don't override mid-session navigation
                    if (currentScreen == null) {
                        currentScreen = if (user == null) Screen.SETUP else Screen.HOME
                    }
                    // If user just completed setup, go home
                    if (currentScreen == Screen.SETUP && user != null) {
                        currentScreen = Screen.HOME
                    }
                }

                when (currentScreen) {
                    null -> SplashScreen()

                    Screen.SETUP -> SetupScreen(userViewModel)

                    Screen.HOME -> HomeScreen(
                        userViewModel    = userViewModel,
                        mealViewModel    = mealViewModel,
                        workoutViewModel = workoutViewModel,
                        onNavigateToMeals = {
                            activeDate = todayAsString()
                            currentScreen = Screen.FOOD_SEARCH
                        },
                        onNavigateToHistory = { currentScreen = Screen.MEAL_HISTORY },
                        onNavigateToWorkout = {
                            activeDate = todayAsString()
                            currentScreen = Screen.WORKOUT
                        }
                    )

                    Screen.FOOD_SEARCH -> MealLoggingScreen(
                        foodViewModel = foodViewModel,
                        mealViewModel = mealViewModel,
                        targetDate    = activeDate,
                        onDismiss     = { currentScreen = Screen.HOME }
                    )

                    Screen.MEAL_HISTORY -> MealHistoryScreen(
                        mealViewModel = mealViewModel,
                        onBack        = { currentScreen = Screen.HOME }
                    )

                    //Screen.WORKOUT -> SplashScreen()

                    Screen.WORKOUT -> WorkoutLoggingScreen(
                        exerciseViewModel = exerciseViewModel,
                        workoutViewModel  = workoutViewModel,
                        targetDate        = activeDate,
                        onDismiss         = { currentScreen = Screen.HOME }
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
    MEAL_HISTORY,
    WORKOUT
}
// Splash / Loading Screen
@Composable
fun SplashScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}
