package com.dissy.lizkitchen.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.dissy.lizkitchen.databinding.ActivityProfileBinding
import com.dissy.lizkitchen.repository.UserRepository
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.register.RegisterViewModel
import com.dissy.lizkitchen.utility.Preferences

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val userRepository = UserRepository()
    private val profileViewModel = ProfileViewModel(userRepository)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userId = Preferences.getUserId(this)
        if (userId != null) {
            profileViewModel.getUserData(userId)
            Log.d("ID", userId)
        }
        profileViewModel.userData.observe(this) { userData ->
            if (userData != null) {
                val email = userData.getString("email")
                val phoneNumber = userData.getString("phoneNumber")
                val username = userData.getString("username")
                binding.apply {
                    etUsername.setText(username)
                    etEmail.setText(email)
                    etNotelp.setText(phoneNumber)
                }
            } else {
                Toast.makeText(this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnToHome.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnToLogout.setOnClickListener {
            Preferences.logout(this)
            Intent(this, LoginActivity::class.java).also {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                startActivity(it)
                finish()
            }
        }

        binding.btnUpdateData.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                startActivity(it)
                finish()
            }
        }
    }
}