package com.dissy.lizkitchen.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.dissy.lizkitchen.ui.admin.AdminCakeActivity
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.register.RegisterActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val usernameCheck = Preferences.getUsername(this)
        if (Preferences.checkUsername(this) && usernameCheck == "admin") {
            Intent(this, AdminCakeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else if (Preferences.checkUsername(this)) {
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

            if (username == "admin" && password == "admin") {
                Preferences.saveUsername(username, this)
                Intent(this, AdminCakeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val loginResult = loginUser(username, password)
                    handleLoginResult(loginResult)
                }
            }
        }
    }

    private fun handleLoginResult(result: Pair<Boolean, String?>) {
        if (result.first) {
            val username = Preferences.getUsername(this)
            Toast.makeText(this, "Login berhasil, $username", Toast.LENGTH_SHORT).show()
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else {
            Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loginUser(username: String, password: String): Pair<Boolean, String?> {
        return try {
            val userQuery = usersCollection.whereEqualTo("username", username).get().await()

            if (!userQuery.isEmpty) {
                val userDoc = userQuery.documents[0]

                val storedPassword = userDoc.getString("password")
                if (storedPassword == password) {
                    val userId = userDoc.getString("userId")
                    if (userId != null) {
                        Log.d("USER ID LOGIN", userId)
                        Preferences.saveUsername(username, this)
                        Preferences.saveUserId(userId, this)
                    }
                    Pair(true, userId)
                } else {
                    Pair(false, null)
                }
            } else {
                Pair(false, null)
            }
        } catch (e: Exception) {
            Pair(false, null)
        }
    }

}