package com.fitlife.app.data.database

import androidx.room.*
import com.fitlife.app.domain.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: Int): User?
    @Query("SELECT * FROM User LIMIT 1")
    suspend fun getUser(): User?
    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User): Int
}