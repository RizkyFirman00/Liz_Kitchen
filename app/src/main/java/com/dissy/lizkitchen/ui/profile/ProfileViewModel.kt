package com.dissy.lizkitchen.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dissy.lizkitchen.repository.UserRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<DocumentSnapshot?>()
    val userData: MutableLiveData<DocumentSnapshot?>
        get() = _userData

    fun getUserData(userId: String) {
        viewModelScope.launch {
            val data = userRepository.getUserData(userId)
            _userData.value = data
        }
    }

}