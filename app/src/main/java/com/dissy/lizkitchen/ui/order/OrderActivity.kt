package com.dissy.lizkitchen.ui.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.adapter.user.HomeOrderUserAdapter
import com.dissy.lizkitchen.adapter.user.HomeOrderUserCakeAdapter
import com.dissy.lizkitchen.databinding.ActivityHistoryBinding
import com.dissy.lizkitchen.databinding.RvOrderCakeUserBinding
import com.dissy.lizkitchen.databinding.RvOrderUserBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.model.Order
import com.dissy.lizkitchen.model.User
import com.dissy.lizkitchen.ui.cart.CartActivity
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.profile.ProfileActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class OrderActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var orderId : String
    private lateinit var orderAdapter: HomeOrderUserAdapter
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.progressBar2.visibility = android.view.View.VISIBLE
        Preferences.getUserId(this)?.let { userId ->
            db.collection("users").document(userId)
                .collection("orders").get()
                .addOnSuccessListener { result ->
                    val orderList = mutableListOf<Order>()
                    for (document in result) {
                        val cartItemsArray =
                            document.get("cart") as? ArrayList<HashMap<String, Any>>
                        val cartItems = cartItemsArray?.map { map ->
                            val cakeMap = map["cake"] as? HashMap<*, *>
                            Cart(
                                cakeId = cakeMap?.get("documentId") as? String ?: "",
                                cake = Cake(
                                    documentId = cakeMap?.get("documentId") as? String ?: "",
                                    harga = cakeMap?.get("harga") as? String ?: "",
                                    imageUrl = cakeMap?.get("imageUrl") as? String ?: "",
                                    namaKue = cakeMap?.get("namaKue") as? String ?: "",
                                    stok = (cakeMap?.get("stok") as? Long) ?: 0
                                ),
                                jumlahPesanan = map["jumlahPesanan"] as? Long ?: 0
                            )
                        } ?: listOf()
                        val userInfo = document.get("user") as? HashMap<String, Any>
                        val order = Order(
                            cart = cartItems,
                            orderId = document.getString("orderId") ?: "",
                            status = document.getString("status") ?: "",
                            totalPrice = document.getLong("totalPrice") ?: 0,
                            metodePengambilan = document.getString("metodePengambilan") ?: "",
                            user = userInfo?.let {
                                User(
                                    userId = it["userId"] as? String ?: "",
                                    username = it["username"] as? String ?: "",
                                    email = it["email"] as? String ?: "",
                                    phoneNumber = it["phoneNumber"] as? String ?: "",
                                    alamat = it["alamat"] as? String ?: ""
                                )
                            } ?: User()
                        )
                        Log.d("AdminUserAct", "order: $order")
                        orderId = order.orderId
                        Log.d("AdminUserAct", "orderId: $orderId")
                        orderList.add(order)
                    }
                    orderAdapter.submitList(orderList)
                    binding.progressBar2.visibility = View.GONE
                    if (orderList.isEmpty()) {
                        binding.emptyCart.visibility = View.VISIBLE
                    } else {
                        binding.emptyCart.visibility = View.GONE
                    }
                }.addOnFailureListener { exception ->
                    Log.d("TAG", "Error getting documents: ", exception)
                }
        }

        orderAdapter = HomeOrderUserAdapter {
            Intent(this, OrderDetailActivity::class.java).also { intent ->
                intent.putExtra("orderId", it)
                startActivity(intent)
            }
        }
        binding.rvOrder.adapter = orderAdapter
        binding.rvOrder.layoutManager = LinearLayoutManager(this)


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
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }

                R.id.navigation_cart -> {
                    Intent(this, CartActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }

                R.id.navigation_history -> {
                    true
                }
            }
            true
        }

    }
}