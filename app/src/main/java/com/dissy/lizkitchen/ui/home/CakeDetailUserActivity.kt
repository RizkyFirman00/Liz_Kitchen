package com.dissy.lizkitchen.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityCakeDetailUserBinding

class CakeDetailUserActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCakeDetailUserBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}