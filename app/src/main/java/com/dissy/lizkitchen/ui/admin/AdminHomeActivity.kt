package com.dissy.lizkitchen.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.databinding.ActivityAdminHomeBinding
import com.dissy.lizkitchen.ui.admin.cake.AdminCakeActivity
import com.dissy.lizkitchen.ui.admin.user.AdminUserOrderActivity
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.utility.Preferences

class AdminHomeActivity : AppCompatActivity() {
    private val binding by lazy {ActivityAdminHomeBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToCakes.setOnClickListener {
            Intent(this, AdminCakeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnToUsers.setOnClickListener {
            Intent(this, AdminUserOrderActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnToLogout.setOnClickListener {
            Preferences.logout(this)
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}