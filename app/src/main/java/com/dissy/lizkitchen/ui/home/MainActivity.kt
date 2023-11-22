package com.dissy.lizkitchen.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dissy.lizkitchen.databinding.ActivityMainBinding
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.profile.ProfileActivity
import com.dissy.lizkitchen.ui.register.RegisterActivity
import com.dissy.lizkitchen.utility.Preferences

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userId = Preferences.getUserId(this)

        binding.btnToProfile.setOnClickListener {
            Intent(this, ProfileActivity::class.java).also {
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

    }
}