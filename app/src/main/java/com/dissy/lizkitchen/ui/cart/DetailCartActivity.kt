package com.dissy.lizkitchen.ui.cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityDetailCartBinding
import com.dissy.lizkitchen.ui.home.MainActivity

class DetailCartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailCartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToHome.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}