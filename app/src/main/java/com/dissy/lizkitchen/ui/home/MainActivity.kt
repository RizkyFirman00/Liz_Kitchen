package com.dissy.lizkitchen.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.adapter.user.HomeUserAdapter
import com.dissy.lizkitchen.databinding.ActivityMainBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.ui.cart.CartActivity
import com.dissy.lizkitchen.ui.order.OrderActivity
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

        Preferences.getUserInfo(this)?.let {
            Log.d("User Info", "$it")
        }

        userAdapter = HomeUserAdapter {
            navigateToDetailDataActivity(it)
        }
        binding.rvUser.adapter = userAdapter
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        fetchDataAndUpdateRecyclerView()

        binding.btnToProfile.setOnClickListener {
            Intent(this, ProfileActivity::class.java).also {
                startActivity(it)
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

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_cart -> {
                    Intent(this, CartActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                R.id.navigation_history -> {
                    Intent(this, OrderActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            }
            true
        }
    }
    private fun fetchDataAndUpdateRecyclerView() {
        binding.progressBar2.visibility = android.view.View.VISIBLE
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
                binding.progressBar2.visibility = android.view.View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "Error fetching data", exception)
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDetailDataActivity(cakeId: String) {
        val intent = Intent(this, CakeDetailUserActivity::class.java)
        intent.putExtra("cakeId", cakeId)
        startActivity(intent)
    }
}