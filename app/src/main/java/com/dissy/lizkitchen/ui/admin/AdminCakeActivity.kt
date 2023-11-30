package com.dissy.lizkitchen.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.adapter.HomeAdminCakeAdapter
import com.dissy.lizkitchen.databinding.ActivityAdminBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminCakeActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val cakesCollection = db.collection("cakes")
    private lateinit var adminAdapter: HomeAdminCakeAdapter
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToLogout.setOnClickListener {
            Preferences.logout(this)
            Intent(this, LoginActivity::class.java).also {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                startActivity(it)
                finish()
            }
        }

        binding.btnAddData.setOnClickListener {
            Intent(this, AdminAddActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        adminAdapter = HomeAdminCakeAdapter {
            navigateToDetailDataActivity(it)
        }
        binding.rvAdmin.adapter = adminAdapter
        binding.rvAdmin.layoutManager = LinearLayoutManager(this)
        fetchDataAndUpdateRecyclerView()
    }
    private fun fetchDataAndUpdateRecyclerView() {
        cakesCollection.get()
            .addOnSuccessListener { result ->
                val cakesList = mutableListOf<Cake>()
                for (document in result) {
                    val cakeData = document.toObject(Cake::class.java)
                    cakesList.add(cakeData)
                }
                adminAdapter.submitList(cakesList)
                adminAdapter.sortDataByName()
                Log.d("AdminActivity", "Fetched data: $cakesList")
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "Error fetching data", exception)
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDetailDataActivity(cakeId: String) {
        val intent = Intent(this, CakeDetailActivity::class.java)
        intent.putExtra("documentId", cakeId)
        startActivity(intent)
    }
}