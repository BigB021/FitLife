package com.fitlife.app.data.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitlife.app.domain.model.DailySummary
import com.fitlife.app.domain.model.Exercise
import com.fitlife.app.domain.model.FoodEntry
import com.fitlife.app.domain.model.Meal
import com.fitlife.app.domain.model.User
import com.fitlife.app.domain.model.WorkoutExercise
import com.fitlife.app.domain.model.WorkoutSession

@Database(
    entities = [User::class, FoodEntry::class, Exercise::class, DailySummary::class, WorkoutExercise::class, WorkoutSession::class, Meal::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    abstract fun WorkoutDao(): WorkoutDao
    abstract fun FoodDao(): FoodDao

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitlife_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}