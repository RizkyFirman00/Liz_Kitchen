package com.dissy.lizkitchen.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityAdminAddBinding
import com.dissy.lizkitchen.databinding.ActivityLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminAddActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val cakeCollection = db.collection("cakes")
    private val binding by lazy { ActivityAdminAddBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}