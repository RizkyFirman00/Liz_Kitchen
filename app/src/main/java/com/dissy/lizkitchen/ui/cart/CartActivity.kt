package com.dissy.lizkitchen.ui.cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.adapter.user.HomeCartUserAdapter
import com.dissy.lizkitchen.databinding.ActivityCartBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.model.User
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.ui.login.LoginActivity
import com.dissy.lizkitchen.ui.order.OrderActivity
import com.dissy.lizkitchen.ui.profile.ProfileActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CartActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private var totalPrice: Long = 0
    private lateinit var cartAdapter: HomeCartUserAdapter
    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userId = Preferences.getUserId(this)

        cartAdapter = HomeCartUserAdapter()
        binding.rvCart.adapter = cartAdapter
        binding.rvCart.layoutManager = LinearLayoutManager(this)
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

        binding.btnCheckout.setOnClickListener {
            if (userId != null) {
                db.collection("users").document(userId).collection("cart").get()
                    .addOnSuccessListener { result ->
                        val cartList = mutableListOf<Cart>()
                        for (document in result) {
                            val cakeId = document.id
                            val jumlahPesanan = document.get("jumlahPesanan") as Long
                            val cakeDataMap = document.get("cake") as HashMap<String, Any>

                            val harga = cakeDataMap["harga"] as String
                            val namaKue = cakeDataMap["namaKue"] as String
                            val imageUrl = cakeDataMap["imageUrl"] as String
                            val stok = (cakeDataMap["stok"] as Long)

                            val cakeData = Cake(cakeId, harga, imageUrl, namaKue, stok)
                            cartList.add(Cart(cakeId, cakeData, jumlahPesanan))
                        }
                        val userInfo = Preferences.getUserInfo(this)
                        val user = User(
                            userId = userId,
                            username = userInfo?.username ?: "Nama Pengguna",
                            email = userInfo?.email ?: "Email Pengguna",
                            phoneNumber = userInfo?.phoneNumber ?: "Nomor Telepon Pengguna",
                            alamat = userInfo?.alamat ?: "Alamat Pengguna"
                        )

                        val orderId = generateOrderId()
                        val order = hashMapOf(
                            "orderId" to orderId,
                            "cart" to cartList,
                            "status" to "Menunggu Pembayaran",
                            "totalPrice" to totalPrice,
                            "user" to user
                        )
                        Log.d("CartActivity", "Isi Cart : $cartList")

                        // Menyimpan data order ke dalam data users di collection orders
                        db.collection("users")
                            .document(userId)
                            .collection("orders")
                            .document(orderId)
                            .set(order)
                            .addOnSuccessListener {
                                Log.d("CartActivity", "Order berhasil dibuat")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("CartActivity", "Error adding order", exception)
                                Toast.makeText(
                                    this,
                                    "Error adding order: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        // Menyimpan data order ke collection Orders
                        db.collection("orders")
                            .document(orderId)
                            .set(order)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Pesanan berhasil dibuat",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearCart(userId)

                                Intent(this, DetailCartActivity::class.java).also {
                                    it.putExtra("orderId", orderId)
                                    startActivity(it)
                                    finish()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("CartActivity", "Error adding order", exception)
                                Toast.makeText(
                                    this,
                                    "Error adding order: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
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
                    true
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

    override fun onBackPressed() {
        super.onBackPressed()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun formatAndDisplayCurrency(value: String): String {
        // Tandai apakah angka negatif
        val isNegative = value.startsWith("-")
        val cleanValue = if (isNegative) value.substring(1) else value

        // Format ulang angka dengan menambahkan titik setiap 3 angka
        val stringBuilder = StringBuilder(cleanValue)
        val length = stringBuilder.length
        var i = length - 3
        while (i > 0) {
            stringBuilder.insert(i, ".")
            i -= 3
        }

        // Tambahkan tanda minus kembali jika angka negatif
        val formattedText = if (isNegative) {
            stringBuilder.insert(0, "-").toString()
        } else {
            stringBuilder.toString()
        }

        return formattedText
    }

    private fun fetchDataAndUpdateRecyclerView() {
        val userId = Preferences.getUserId(this)
        if (userId != null) {
            db.collection("users").document(userId).collection("cart").get()
                .addOnSuccessListener { result ->
                    val cartList = mutableListOf<Cart>()
                    for (document in result) {
                        val cakeId = document.id
                        val jumlahPesanan = document.get("jumlahPesanan") as Long
                        val cakeDataMap = document.get("cake") as HashMap<String, Any>

                        val harga = cakeDataMap["harga"] as String
                        val namaKue = cakeDataMap["namaKue"] as String
                        val imageUrl = cakeDataMap["imageUrl"] as String
                        val stok = (cakeDataMap["stok"] as Long)

                        val cakeData = Cake(cakeId, harga, imageUrl, namaKue, stok)
                        cartList.add(Cart(cakeId, cakeData, jumlahPesanan))
                        totalPrice += harga.replace(".", "").toLong() * jumlahPesanan
                    }

                    binding.tvPriceSum.text = formatAndDisplayCurrency(totalPrice.toString())
                    cartAdapter.submitList(cartList)
                    if (cartList.isEmpty()) {
                        binding.emptyCart.visibility = View.VISIBLE
                        binding.linearLayout1.visibility = View.GONE
                    } else {
                        binding.emptyCart.visibility = View.GONE
                        binding.linearLayout1.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("CartActivity", "Error fetching data", exception)
                    Toast.makeText(
                        this,
                        "Error fetching data: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun generateOrderId(): String {
        return "ORDER-${System.currentTimeMillis()}"
    }

    private fun clearCart(userId: String) {
        db.collection("users").document(userId).collection("cart").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("users").document(userId)
                        .collection("cart").document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(
                                "CartActivity",
                                "DocumentSnapshot successfully deleted!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                "CartActivity",
                                "Error deleting document",
                                e
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "CartActivity",
                    "Error fetching data",
                    exception
                )
                Toast.makeText(
                    this,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}