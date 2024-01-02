package com.dissy.lizkitchen.ui.admin.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() {
    private val binding by lazy {ActivityReportBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}