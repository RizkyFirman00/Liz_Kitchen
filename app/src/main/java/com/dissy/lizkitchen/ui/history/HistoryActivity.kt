package com.dissy.lizkitchen.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}