package com.dissy.lizkitchen.ui.admin.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dissy.lizkitchen.databinding.ActivityUserDetailBinding

class UserDetailOrderActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUserDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("orderId")
        Log.d("orderId", orderId.toString())

        binding.btnToHome.setOnClickListener {
            finish()
        }
    }
}