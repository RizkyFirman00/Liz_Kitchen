package com.dissy.lizkitchen.ui.register
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.dissy.lizkitchen.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun registerUser(email: String, phoneNumber: String, username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val registrationSuccessful = userRepository.registerUser(email, phoneNumber, username, password)
            callback(registrationSuccessful)
        }
    }
}
