package com.dissy.lizkitchen.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.dissy.lizkitchen.databinding.ActivityMainBinding
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToregister.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                Toast.makeText(this, "Berhasil Login", Toast.LENGTH_SHORT).show()
                startActivity(it)
                finish()
            }
        }
    }
}