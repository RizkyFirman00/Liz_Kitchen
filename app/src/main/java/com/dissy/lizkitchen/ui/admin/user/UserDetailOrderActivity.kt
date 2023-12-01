package com.dissy.lizkitchen.ui.admin.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.adapter.admin.CartDetailUserAdapter
import com.dissy.lizkitchen.databinding.ActivityUserDetailBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.model.Order
import com.dissy.lizkitchen.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDetailOrderActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val cakesCollection = db.collection("orders")
    private lateinit var orderId: String
    private lateinit var cartDetailUserAdapter: CartDetailUserAdapter
    private val binding by lazy { ActivityUserDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        orderId = intent.getStringExtra("orderId").toString()
        Log.d("orderId", orderId)

        cartDetailUserAdapter = CartDetailUserAdapter()
        binding.rvDetailOrderItem.adapter = cartDetailUserAdapter
        binding.rvDetailOrderItem.layoutManager = LinearLayoutManager(this)
        fetchDataAndUpdateRecyclerView(orderId)

        db.collection("orders").document(orderId).get()
            .addOnSuccessListener {
                val order = Order(
                    cart = listOf(),
                    orderId = it.get("orderId") as? String ?: "",
                    status = it.get("status") as? String ?: "",
                    totalPrice = it.get("totalPrice") as? Long ?: 0,
                    user = User(
                        userId = it.get("userId") as? String ?: "",
                        username = it.get("username") as? String ?: "",
                        phoneNumber = it.get("phoneNumber") as? String ?: "",
                        email = it.get("email") as? String ?: "",
                        password = it.get("password") as? String ?: "",
                        alamat = it.get("alamat") as? String ?: "Belum ada alamat",
                    )
                )
                binding.apply {
                    tvOrderId.text = order.orderId
                    tvStatus.text = order.status
                    when (order.status) {
                        "Selesai" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                        }
                        "Dibatalkan" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
                            binding.btnConfirm.visibility = GONE
                            binding.btnCancel.visibility = GONE
                        }
                        "Menunggu Pembayaran" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
                        }
                        "Sedang Dikirim", "Sudah Dikonfirmasi" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                            binding.btnCancel.visibility = GONE
                        }
                        "Sedang Diproses" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#9C6843"))
                            binding.btnCancel.visibility = GONE
                        }
                    }

                    tvAlamat.text = order.user.alamat
                    tvOrderId.text = order.orderId
                    val priceSum = formatAndDisplayCurrency(order.totalPrice.toString())
                    tvPriceSum.text = priceSum
                }
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

        binding.btnToHome.setOnClickListener {
            finish()
        }

        binding.btnConfirm.setOnClickListener {
            db.collection("orders").document(orderId).update("status", "Sudah Dikonfirmasi")
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil mengkonfirmasi pesanan", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Berhasil mengkonfirmasi pesanan", Toast.LENGTH_SHORT).show()
                    Intent(this, AdminUserOrderActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mengkonfirmasi pesanan", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Error getting documents: ", exception)
                }
        }

        binding.btnCancel.setOnClickListener {
            db.collection("orders").document(orderId).update("status", "Dibatalkan")
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil membatalkan pesanan", Toast.LENGTH_SHORT).show()
                    Intent(this, AdminUserOrderActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal membatalkan pesanan", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Error getting documents: ", exception)
                }
        }
    }

    private fun fetchDataAndUpdateRecyclerView(orderId: String) {
        db.collection("orders").document(orderId).get()
            .addOnSuccessListener {
                // masukkan datanya ke dalam recyclerview
                val cartList = mutableListOf<Cart>()
                val cartItemsArray = it.get("cart") as? ArrayList<HashMap<String, Any>>
                val cartItems = cartItemsArray?.map { map ->
                    val cakeMap = map["cake"] as? HashMap<String, Any>
                    Cart(
                        cakeId = map["cakeId"] as? String ?: "",
                        cake = Cake(
                            documentId = cakeMap?.get("cakeId") as? String ?: "",
                            namaKue = cakeMap?.get("namaKue") as? String ?: "",
                            harga = cakeMap?.get("harga") as? String ?: "",
                            stok = cakeMap?.get("stok") as? Long ?: 0,
                            imageUrl = cakeMap?.get("imageUrl") as? String ?: ""
                        ),
                        jumlahPesanan = map["jumlahPesanan"] as? Long ?: 0
                    )
                } ?: listOf()
                cartList.addAll(cartItems)
                cartDetailUserAdapter.submitList(cartItems)
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
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
}