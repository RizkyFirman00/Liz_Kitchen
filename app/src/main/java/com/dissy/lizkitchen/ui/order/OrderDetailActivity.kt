package com.dissy.lizkitchen.ui.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.adapter.admin.CartDetailUserAdapter
import com.dissy.lizkitchen.databinding.ActivityOrderDetailBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.model.Order
import com.dissy.lizkitchen.model.User
import com.dissy.lizkitchen.ui.konfirmasi.ConfirmActivity
import com.dissy.lizkitchen.utility.Preferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderDetailActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var userId: String
    private lateinit var orderId: String
    private lateinit var cartDetailUserAdapter: CartDetailUserAdapter
    private val binding by lazy { ActivityOrderDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userId = Preferences.getUserId(this).toString()
        orderId = intent.getStringExtra("orderId").toString()
        Log.d("TAG", "onCreate Detail order $orderId"
        )

        cartDetailUserAdapter = CartDetailUserAdapter()
        binding.rvDetailOrderItem.adapter = cartDetailUserAdapter
        binding.rvDetailOrderItem.layoutManager = LinearLayoutManager(this)
        fetchDataAndUpdateRecyclerView(orderId)

        db.collection("users").document(userId).collection("orders").document(orderId).get()
            .addOnSuccessListener {
                val cartItemsArray = it.get("cart") as? ArrayList<HashMap<String, Any>>
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
                val userInfo = it.get("user") as? HashMap<String, Any>
                val order = Order(
                    cart = cartItems,
                    orderId = it.getString("orderId") ?: "",
                    status = it.getString("status") ?: "",
                    totalPrice = it.getLong("totalPrice") ?: 0,
                    metodePengambilan = it.getString("metodePengambilan") ?: "",
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
                Log.d("TAG", "Error getting documents: $order")
                binding.apply {
                    tvOrderId.text = order.orderId
                    tvStatus.text = order.status
                    when (order.status) {
                        "Selesai" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                        }

                        "Dibatalkan" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
                            binding.btnConfirm.visibility = View.GONE
                            binding.btnCancel.visibility = View.GONE
                        }

                        "Menunggu Pembayaran" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
                        }

                        "Sedang Dikirim", "Sudah Dikonfirmasi" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                            binding.btnCancel.visibility = View.GONE
                        }

                        "Sedang Diproses" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#9C6843"))
                            binding.btnCancel.visibility = View.GONE
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
            Intent(this, ConfirmActivity::class.java).also {
                it.putExtra("orderId", orderId)
                startActivity(it)
            }
        }

        binding.btnCancel.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Konfirmasi Pembatalan")
            builder.setMessage("Apakah Anda yakin ingin membatalkan pesanan ini?")

            builder.setPositiveButton("Ya") { _, _ ->
                cancelOrder()
            }

            builder.setNegativeButton("Tidak") { _, _ ->

            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun fetchDataAndUpdateRecyclerView(orderId: String) {
        Log.d("TAG", "fetchDataAndUpdateRecyclerView: $orderId")
        db.collection("users").document(userId).collection("orders").document(orderId).get()
            .addOnSuccessListener {
                // masukkan datanya ke dalam recyclerview
                val cartList = mutableListOf<Cart>()
                val cartItemsArray = it.get("cart") as? ArrayList<HashMap<String, Any>>
                val cartItems = cartItemsArray?.map { map ->
                    val cakeMap = map["cake"] as? HashMap<String, Any>
                    Cart(
                        cakeId = map["cakeId"] as? String ?: "",
                        cake = Cake(
                            documentId = cakeMap?.get("documentId") as? String ?: "",
                            namaKue = cakeMap?.get("namaKue") as? String ?: "",
                            harga = cakeMap?.get("harga") as? String ?: "",
                            stok = cakeMap?.get("stok") as? Long ?: 0,
                            imageUrl = cakeMap?.get("imageUrl") as? String ?: ""
                        ),
                        jumlahPesanan = map["jumlahPesanan"] as? Long ?: 0
                    )
                } ?: listOf()
                cartList.addAll(cartItems)
                Log.d("TAG", "Cart List Detail Order: $cartList")
                cartDetailUserAdapter.submitList(cartItems)
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun cancelOrder() {
        db.collection("users").document(userId).collection("orders").document(orderId)
            .update("status", "Dibatalkan")
            .addOnSuccessListener {
                Toast.makeText(this, "Pesanan berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OrderActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Pesanan gagal dibatalkan", Toast.LENGTH_SHORT).show()
            }

        db.collection("orders").document(orderId).update("status", "Dibatalkan")
            .addOnSuccessListener {
                Toast.makeText(this, "Pesanan berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OrderActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Pesanan gagal dibatalkan", Toast.LENGTH_SHORT).show()
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