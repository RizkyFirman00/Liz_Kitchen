package com.dissy.lizkitchen.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityRegisterBinding
import com.dissy.lizkitchen.repository.UserRepository
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val userRepository = UserRepository()
    private val registerViewModel = RegisterViewModel(userRepository)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnTologin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val phoneNumber = binding.etNotelp.text.toString()
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            registerViewModel.registerUser(email, phoneNumber, username, password) { registrationSuccessful ->
                if (registrationSuccessful) {
                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    Intent(this@RegisterActivity, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}