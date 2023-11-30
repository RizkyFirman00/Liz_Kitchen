package com.dissy.lizkitchen.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityAdminUserBinding
import com.dissy.lizkitchen.ui.login.LoginActivity

class AdminUserActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAdminUserBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToLogout.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}