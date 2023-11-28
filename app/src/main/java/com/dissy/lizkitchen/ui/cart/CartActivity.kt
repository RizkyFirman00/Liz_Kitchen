package com.dissy.lizkitchen.ui.cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.adapter.CartUserAdapter
import com.dissy.lizkitchen.adapter.HomeAdminAdapter
import com.dissy.lizkitchen.databinding.ActivityCartBinding
import com.dissy.lizkitchen.databinding.ActivityDetailCartBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.ui.home.MainActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CartActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private var totalPrice: Long = 0
    private lateinit var cartAdapter: CartUserAdapter
    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        cartAdapter = CartUserAdapter()
        binding.rvCart.adapter = cartAdapter
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        fetchDataAndUpdateRecyclerView()

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
//                R.id.navigation_history -> {
//                    Intent(this, HistoryActivity::class.java).also {
//                        startActivity(it)
//                        finish()
//                    }
//                }
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
                        db.collection("cakes").document(cakeId).get().addOnSuccessListener { result ->
                            val cakeName = result.get("namaKue") as String
                            val cakePrice = result.get("harga") as String
                            val cakeImage = result.get("imageUrl") as String
                            val cakeStock = result.get("stok") as String
                            val cake = Cake(cakeId, cakePrice, cakeImage, cakeName, cakeStock)
                            val cartData = Cart(cakeId, cake, jumlahPesanan)
                            cartList.add(cartData)
                            totalPrice += cakePrice.replace(".", "").toLong() * jumlahPesanan
                            binding.tvPriceSum.text = formatAndDisplayCurrency(totalPrice.toString())
                            cartAdapter.submitList(cartList)
                            Log.d("CartActivity", "Fetched data: $cartList")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("CartActivity", "Error fetching data", exception)
                    Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}