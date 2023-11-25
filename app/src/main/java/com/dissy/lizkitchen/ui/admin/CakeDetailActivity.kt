package com.dissy.lizkitchen.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityCakeDetailBinding

class CakeDetailActivity : AppCompatActivity() {
    private val binding by lazy {ActivityCakeDetailBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val cakeName = intent.getStringExtra("cakeName")
        binding.cakeName.text = cakeName
    }
}