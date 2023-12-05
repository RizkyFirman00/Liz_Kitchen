package com.dissy.lizkitchen.ui.admin.user

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dissy.lizkitchen.adapter.admin.CartDetailUserAdapter
import com.dissy.lizkitchen.databinding.ActivityUserDetailBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.model.Order
import com.dissy.lizkitchen.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminUserOrderDetailActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var orderId: String
    private lateinit var userId: String
    private lateinit var cartDetailUserAdapter: CartDetailUserAdapter
    private lateinit var orderStatus: String
    private val binding by lazy { ActivityUserDetailBinding.inflate(layoutInflater) }
    @SuppressLint("SimpleDateFormat")
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
            .addOnSuccessListener { orderDocument ->
                // ambil semua data user
                val cartItemsArray = orderDocument.get("cart") as? ArrayList<HashMap<String, Any>>
                val cartItems = cartItemsArray?.map { map ->
                    val cakeMap = map["cake"] as? HashMap<*, *>
                    Cart(
                        cakeId = map["cakeId"] as? String ?: "",
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
                val userInfo = orderDocument.get("user") as? HashMap<String, Any>
                val order = Order(
                    cart = cartItems,
                    orderId = orderDocument.getString("orderId") ?: "",
                    status = orderDocument.getString("status") ?: "",
                    totalPrice = orderDocument.getLong("totalPrice") ?: 0,
                    metodePengambilan = orderDocument.getString("metodePengambilan") ?: "",
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
                orderStatus = order.status
                userId = order.user.userId.toString()

                binding.apply {
                    tvOrderId.text = order.orderId
                    tvStatus.text = order.status
                    when (order.status) {
                        "Selesai" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                            btnCancel.visibility = GONE
                            btnConfirm.visibility = GONE
                        }

                        "Dibatalkan" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
                            btnCancel.visibility = GONE
                            btnConfirm.visibility = GONE
                        }

                        "Menunggu Pembayaran" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
                        }

                        "Sedang Dikirim" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                            btnCancel.visibility = GONE
                        }

                        "Sudah Dikonfirmasi" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
                            btnCancel.visibility = GONE
                            btnConfirm.visibility = GONE
                            btnProcess.visibility = VISIBLE
                        }

                        "Sedang Diproses" -> {
                            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#9C6843"))
                            btnCancel.visibility = GONE
                            btnConfirm.visibility = GONE
                            when (order.metodePengambilan) {
                                "Ambil Sendiri" -> {
                                    btnShipping.visibility = GONE
                                    btnReady.visibility = VISIBLE
                                    btnReady.gravity = 1
                                }
                                "Pesan Antar" -> {
                                    btnShipping.visibility = VISIBLE
                                    btnReady.visibility = GONE
                                    btnReady.gravity = 1
                                }
                            }
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
            val time = System.currentTimeMillis()
            val date = java.sql.Date(time)
            val sdf = java.text.SimpleDateFormat("dd-MM-yyyy")
            val formattedDate = sdf.format(date)
            db.collection("orders").document(orderId).update(
                "tanggalOrder", formattedDate,
                "status", "Sudah Dikonfirmasi"
            )
                .addOnSuccessListener {

                    cutStock(orderId)
                    db.collection("users").document(userId).collection("orders").document(orderId)
                        .update(
                            "tanggalOrder", formattedDate,
                            "status", "Sudah Dikonfirmasi"
                        )
                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
                    Toast.makeText(this, "Berhasil mengkonfirmasi pesanan", Toast.LENGTH_SHORT)
                        .show()
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

        binding.btnProcess.setOnClickListener {
            db.collection("orders").document(orderId).update("status", "Sedang Diproses")
                .addOnSuccessListener {
                    db.collection("users").document(userId).collection("orders").document(orderId)
                        .update("status", "Sedang Diproses")
                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
                    Toast.makeText(this, "Berhasil memproses pesanan", Toast.LENGTH_SHORT).show()
                    Intent(this, AdminUserOrderActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal memproses pesanan", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Error getting documents: ", exception)
                }
        }

        binding.btnShipping.setOnClickListener {
            db.collection("orders").document(orderId).update("status", "Sedang Dikirim")
                .addOnSuccessListener {
                    db.collection("users").document(userId).collection("orders").document(orderId)
                        .update("status", "Sedang Dikirim")
                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
                    Toast.makeText(this, "Berhasil mengirim pesanan", Toast.LENGTH_SHORT).show()
                    Intent(this, AdminUserOrderActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mengirim pesanan", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Error getting documents: ", exception)
                }
        }

        binding.btnReady.setOnClickListener {
            db.collection("orders").document(orderId).update("status", "Sedang Dikirim")
                .addOnSuccessListener {
                    db.collection("users").document(userId).collection("orders").document(orderId)
                        .update("status", "Sedang Dikirim")
                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
                    Toast.makeText(this, "Berhasil mengirim pesanan", Toast.LENGTH_SHORT).show()
                    Intent(this, AdminUserOrderActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mengirim pesanan", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "Error getting documents: ", exception)
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

    private fun cancelOrder(){
        db.collection("orders").document(orderId).update("status", "Dibatalkan")
            .addOnSuccessListener {
                db.collection("users").document(userId).collection("orders").document(orderId)
                    .update("status", "Dibatalkan")
                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
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

    private fun cutStock(orderId: String) {
        db.collection("orders").document(orderId).get()
            .addOnSuccessListener { orderDocument ->
                val cartItemsArray = orderDocument.get("cart") as? ArrayList<HashMap<String, Any>>
                val cartItems = cartItemsArray?.map { map ->
                    Cart(
                        cakeId = map["cakeId"] as? String ?: "",
                        cake = map["cake"] as? Cake ?: Cake(),
                        jumlahPesanan = map["jumlahPesanan"] as? Long ?: 0
                    )
                } ?: listOf()
                Log.d("CutStock", "cartItems: $cartItems")
                for (cartItem in cartItems) {
                    val cakeId = cartItem.cakeId
                    val jumlahPesanan = cartItem.jumlahPesanan
                    Log.d("CutStock", "cartItems: $cakeId")
                    Log.d("CutStock", "cartItems: $jumlahPesanan")

                    // Mendapatkan referensi ke dokumen kue di koleksi "cakes"
                    val cakeDocRef = db.collection("cakes").document(cakeId)

                    // Mengambil data kue saat ini
                    cakeDocRef.get().addOnSuccessListener { cakeDocument ->
                        // Mendapatkan stok kue saat ini
                        val currentStock = cakeDocument.getLong("stok") ?: 0

                        // Memastikan stok mencukupi untuk dipotong
                        if (currentStock >= jumlahPesanan) {
                            // Menghitung sisa stok setelah dipotong
                            val newStock = currentStock - jumlahPesanan

                            // Memperbarui stok kue di koleksi "cakes"
                            cakeDocRef.update("stok", newStock)
                                .addOnSuccessListener {
                                    Log.d("TAG", "Stok kue berhasil dipotong untuk cakeId: $cakeId")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("TAG", "Error updating stock for cakeId: $cakeId", e)
                                }
                        } else {
                            Log.e("TAG", "Stok tidak mencukupi untuk cakeId: $cakeId")
                        }
                    }.addOnFailureListener { e ->
                        Log.w("TAG", "Error getting cake document for cakeId: $cakeId", e)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }


}