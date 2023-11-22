package com.dissy.lizkitchen.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.dissy.lizkitchen.repository.UserRepository
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.register.RegisterActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val userRepository = UserRepository()
    private val loginViewModel = LoginViewModel(userRepository)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Preferences.checkUsername(this)) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnToregister.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            loginViewModel.loginUser(username, password)
            loginViewModel.loginResult.observe(this) { (loginSuccessful, userId) ->
                if (loginSuccessful) {
                    if (userId != null) {
                        // Jika login berhasil, simpan userId dan pindah ke MainActivity
                        Preferences.saveUsername(username, this)
                        Preferences.saveUserId(userId, this)

                        Intent(this, MainActivity::class.java).also {
                            Toast.makeText(this, "Selamat datang $username", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(it)
                            finish()
                        }
                    } else {
                        // Jika userId null, ada sesuatu yang tidak beres
                        Toast.makeText(this, "Terjadi kesalahan saat login", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}