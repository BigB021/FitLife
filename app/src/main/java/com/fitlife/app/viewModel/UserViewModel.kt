package com.fitlife.app.viewModel

import androidx.lifecycle.MutableLiveData
import com.fitlife.app.data.repository.UserRepository
import com.fitlife.app.domain.model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    val user = MutableLiveData<User?>()

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.addUser(user)
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            user.value = userRepository.getUser()
        }
    }

    fun deleteUser(user: User){
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

}