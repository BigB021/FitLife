package com.fitlife.app.viewModel

import androidx.lifecycle.MutableLiveData
import com.fitlife.app.data.repository.UserRepository
import com.fitlife.app.domain.model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository): ViewModel() {
    val userVm = MutableLiveData<User?>()

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.addUser(user)
        }
    }

    fun loadUser(id: Int) {
        viewModelScope.launch {
            userVm.value = userRepository.getUserById(id)
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch {
            userRepository.updateUser(user)
            loadUser(user.id)
        }
    }

    fun deleteUser(user: User){
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

}