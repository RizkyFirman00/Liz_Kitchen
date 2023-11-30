package com.dissy.lizkitchen.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.databinding.ActivityAdminHomeBinding

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

        }
    }
}