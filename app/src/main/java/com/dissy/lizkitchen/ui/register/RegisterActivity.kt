package com.dissy.lizkitchen.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dissy.lizkitchen.databinding.ActivityRegisterBinding
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
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
            val alamat = "Belum diisi"

            registerUser(email, phoneNumber, username, password, alamat)
        }
    }

    private fun registerUser(
        email: String,
        phoneNumber: String,
        username: String,
        password: String,
        alamat: String
    ) {
        loadingProgress()
        val user = hashMapOf(
            "email" to email,
            "phoneNumber" to phoneNumber,
            "username" to username,
            "password" to password,
            "alamat" to alamat
        )

        usersCollection.whereEqualTo("username", username).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val existingUser = task.result?.documents

                if (existingUser != null && existingUser.isNotEmpty()) {
                    unLoadingProgress()
                    Toast.makeText(this, "Username sudah dipakai", Toast.LENGTH_SHORT).show()
                } else {
                    // Menggunakan add untuk membuat dokumen baru dengan ID yang dihasilkan oleh Firestore
                    usersCollection.add(user)
                        .addOnCompleteListener { registrationTask ->
                            if (registrationTask.isSuccessful) {
                                // Mendapatkan ID dokumen yang baru dibuat
                                val newUserId = registrationTask.result?.id

                                // Memperbarui userId di dokumen dengan ID yang baru
                                usersCollection.document(newUserId!!)
                                    .update("userId", newUserId)
                                    .addOnSuccessListener {
                                        unLoadingProgress()
                                        Toast.makeText(
                                            this,
                                            "Registrasi berhasil",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Intent(
                                            this@RegisterActivity,
                                            LoginActivity::class.java
                                        ).also {
                                            startActivity(it)
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            this,
                                            "Gagal memperbarui userId",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e(
                                            "RegisterActivity",
                                            "Error updating userId",
                                            exception
                                        )
                                    }
                            } else {
                                Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Error checking existing user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadingProgress() {
        binding.apply {
            progressBar2.visibility = android.view.View.VISIBLE
            etEmail.isEnabled = false
            etNotelp.isEnabled = false
            etUsername.isEnabled = false
            etPassword.isEnabled = false
            btnRegister.isEnabled = false
            btnTologin.isEnabled = false
        }
    }

    private fun unLoadingProgress() {
        binding.apply {
            progressBar2.visibility = android.view.View.GONE
            etEmail.isEnabled = true
            etNotelp.isEnabled = true
            etUsername.isEnabled = true
            etPassword.isEnabled = true
            btnRegister.isEnabled = true
            btnTologin.isEnabled = true
        }
    }
}