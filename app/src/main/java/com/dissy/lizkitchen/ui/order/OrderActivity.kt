package com.dissy.lizkitchen.ui.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityHistoryBinding
import com.dissy.lizkitchen.ui.cart.CartActivity
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.profile.ProfileActivity
import com.dissy.lizkitchen.utility.Preferences

class OrderActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToProfile.setOnClickListener {
            Intent(this, ProfileActivity::class.java).also {
                startActivity(it)
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

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                R.id.navigation_cart -> {
                    Intent(this, CartActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                R.id.navigation_history -> {
                    true
                }
            }
            true
        }

    }
}