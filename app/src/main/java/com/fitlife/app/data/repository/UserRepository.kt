package com.fitlife.app.data.repository

import com.fitlife.app.data.database.UserDao
import com.fitlife.app.domain.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun addUser(user: User): Long = userDao.insertUser(user)
    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)
    suspend fun getUser(): User? = userDao.getUser()
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)


    // BMR (Mifflin-St Jeor)
    fun calculateBMR(user: User): Float {
        return if (user.gender == "Male")
            (10 * user.weight + 6.25 * user.height - 5 * user.age + 5).toFloat()
        else
            (10 * user.weight + 6.25 * user.height - 5 * user.age - 161).toFloat()
    }
    private fun calculateTDEE(user: User): Float {
        val bmr = calculateBMR(user)
        val multiplier = when (user.activityLevel.lowercase()) {
            "sedentary"                         -> 1.2f   // little/no exercise
            "lightly active"                    -> 1.375f // 1–3 days/week
            "moderately active"                 -> 1.55f  // 3–5 days/week
            "very active"                       -> 1.725f // 6–7 days/week
            "extra active"                      -> 1.9f   // physical job + training
            else                                -> 1.2f
        }
        return bmr * multiplier
    }

    private fun calculateCalorieTarget(user: User): Float {
        val tdee = calculateTDEE(user)
        return when (user.goalType.lowercase()) {
            "lose weight"     -> tdee - 500f   // ~0.5 kg/week deficit
            "gain weight"     -> tdee + 300f   // lean bulk surplus
            else              -> tdee           // maintain
        }
    }
    data class MacroTargets(
        val calories: Float,
        val protein: Float,
        val carbs: Float,
        val fat: Float
    )

    fun calculateTargetMacros(user: User): MacroTargets {
        val calories = calculateCalorieTarget(user)
        return MacroTargets(
            calories = calories,
            protein  = (calories * 0.30f) / 4f,
            carbs    = (calories * 0.40f) / 4f,
            fat      = (calories * 0.30f) / 9f
        )
    }

}