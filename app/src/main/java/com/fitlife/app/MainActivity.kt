package com.fitlife.app

import com.fitlife.app.ui.screens.SetupScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import com.fitlife.app.data.database.AppDatabase
import com.fitlife.app.data.repository.UserRepository
import com.fitlife.app.viewModel.UserViewModel
import com.fitlife.app.viewModel.UserViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitlife.app.data.api.FoodAPI
import com.fitlife.app.data.repository.FoodRepository
import com.fitlife.app.data.repository.MealRepository
import com.fitlife.app.data.repository.WorkoutRepository
import com.fitlife.app.ui.screens.SearchScreen
import com.fitlife.app.viewModel.FoodViewModel
import com.fitlife.app.viewModel.FoodViewModelFactory
import com.fitlife.app.viewModel.MealViewModel
import com.fitlife.app.viewModel.MealViewModelFactory
import com.fitlife.app.viewModel.WorkoutViewModel
import com.fitlife.app.viewModel.WorkoutViewModelFactory
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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val foodApi = retrofit.create(FoodAPI::class.java)

        userRepository = UserRepository(db.userDao())
        mealRepository = MealRepository(db.mealDao())
        workoutRepository = WorkoutRepository(db.WorkoutDao())
        foodRepository = FoodRepository(db.FoodDao(), foodApi)

        setContent {
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
            val mealViewModel: MealViewModel = viewModel(factory = MealViewModelFactory(mealRepository))
            val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(workoutRepository))
            val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodRepository))
            val user = userViewModel.user.observeAsState()

            // load once
            LaunchedEffect(Unit) {
                userViewModel.loadUser()
            }

//            if (user.value == null) {
//                SetupScreen(userViewModel)
//            } else {
//                HomeScreen(userViewModel,mealViewModel,workoutViewModel)
//            }

            // Test API
            SearchScreen(foodViewModel) 
        }
    }

}