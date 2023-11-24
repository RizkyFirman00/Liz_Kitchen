package com.dissy.lizkitchen.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityAdminBinding
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.profile.ProfileActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val cakesCollection = db.collection("cakes")
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToLogout.setOnClickListener {
            Preferences.logout(this)
            Intent(this, LoginActivity::class.java).also {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                startActivity(it)
                finish()
            }
        }

        binding.btnAddData.setOnClickListener {
            Intent(this, AdminAddActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}