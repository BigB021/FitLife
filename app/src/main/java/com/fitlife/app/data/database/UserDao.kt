package com.fitlife.app.data.database

import androidx.room.*
import com.fitlife.app.domain.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    // todo: change it to getUserById or something, only testing stuff here
    @Query("SELECT * FROM User LIMIT 1")
    suspend fun getUser(): User?

    @Delete
    suspend fun deleteUser(user: User): Int
}