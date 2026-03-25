package com.fitlife.app.data.repository

import com.fitlife.app.data.database.UserDao
import com.fitlife.app.domain.model.User

// this is a basic implementation just to get things going wkda
class UserRepository(private val userDao: UserDao) {
    suspend fun addUser(user: User) = userDao.insertUser(user)
    suspend fun getUser(): User? = userDao.getUser()
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

}