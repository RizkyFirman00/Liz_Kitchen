package com.dissy.lizkitchen.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.dissy.lizkitchen.databinding.ActivityProfileBinding
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToHome.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnToLogout.setOnClickListener {
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