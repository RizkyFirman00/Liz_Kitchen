package com.dissy.lizkitchen.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dissy.lizkitchen.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val loginResult: LiveData<Pair<Boolean, String?>>
        get() = _loginResult

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            val (loginSuccessful, userId) = userRepository.loginUser(username, password, this)

            _loginResult.value = Pair(loginSuccessful, userId)
        }
    }
}