package com.dissy.lizkitchen.ui.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityDetailCartBinding

class DetailCartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailCartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}