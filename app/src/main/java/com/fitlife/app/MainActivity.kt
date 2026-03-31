package com.fitlife.app

import SetupScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fitlife.app.data.database.AppDatabase
import com.fitlife.app.data.repository.UserRepository
import com.fitlife.app.viewModel.UserViewModel
import com.fitlife.app.viewModel.UserViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(this)
        userRepository = UserRepository(db.userDao())

        setContent {
            val viewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(userRepository)
            )

            SetupScreen(viewModel)
        }
    }

}