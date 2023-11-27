package com.dissy.lizkitchen.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.adapter.HomeAdminAdapter
import com.dissy.lizkitchen.adapter.HomeUserAdapter
import com.dissy.lizkitchen.databinding.ActivityMainBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.ui.admin.CakeDetailActivity
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.profile.ProfileActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val cakesCollection = db.collection("cakes")
    private lateinit var userAdapter: HomeUserAdapter
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userId = Preferences.getUserId(this)
        Log.d("ID Home", "$userId")

        userAdapter = HomeUserAdapter {
            navigateToDetailDataActivity(it)
        }
        binding.rvUser.adapter = userAdapter
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        fetchDataAndUpdateRecyclerView()
        hideProgressBar()

        binding.btnToProfile.setOnClickListener {
            Intent(this, ProfileActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnToLogout.setOnClickListener {
            Preferences.logout(this)
            Intent(this, LoginActivity::class.java).also {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                startActivity(it)
                finish()
            }
        }
    }
    private fun fetchDataAndUpdateRecyclerView() {
        showProgressBar()
        cakesCollection.get()
            .addOnSuccessListener { result ->
                val cakesList = mutableListOf<Cake>()
                for (document in result) {
                    val cakeData = document.toObject(Cake::class.java)
                    cakesList.add(cakeData)
                }
                userAdapter.submitList(cakesList)
                userAdapter.sortDataByName()
                Log.d("AdminActivity", "Fetched data: $cakesList")
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "Error fetching data", exception)
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProgressBar() {
        binding.progressBar2.visibility = android.view.View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar2.visibility = android.view.View.GONE
    }
    private fun navigateToDetailDataActivity(cakeId: String) {
        val intent = Intent(this, CakeDetailUserActivity::class.java)
        intent.putExtra("cakeId", cakeId)
        startActivity(intent)
    }
}