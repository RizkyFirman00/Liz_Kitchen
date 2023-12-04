package com.dissy.lizkitchen.adapter.user

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dissy.lizkitchen.databinding.RvOrderUserBinding
import com.dissy.lizkitchen.model.Order

class HomeOrderUserAdapter(private val onItemClick: (String) -> Unit) : ListAdapter<Order, HomeOrderUserAdapter.HomeUserViewHolder>(
    DiffCallback()
) {
    inner class HomeUserViewHolder(
        private val binding: RvOrderUserBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvStatusPesanan.text = order.status
                when (order.status) {
                    "Selesai" -> {
                        binding.tvStatusPesanan.setTextColor(android.graphics.Color.parseColor("#FF000"))
                    }
                    "Dibatalkan" -> {
                        binding.tvStatusPesanan.setTextColor(android.graphics.Color.parseColor("#D10826"))
                    }
                    "Menunggu Pembayaran" -> {
                        binding.tvStatusPesanan.setTextColor(android.graphics.Color.parseColor("#D10826"))
                    }
                    "Sedang Dikirim", "Sudah Dikonfirmasi" -> {
                        binding.tvStatusPesanan.setTextColor(android.graphics.Color.parseColor("#FF000"))
                    }
                    "Sedang Diproses" -> {
                        binding.tvStatusPesanan.setTextColor(android.graphics.Color.parseColor("#9C6843"))
                    }
                }

                val formatedPrice = formatAndDisplayCurrency(order.totalPrice.toString())
                tvTotalHarga.text = formatedPrice
                tvMetodePengambilan.text = order.metodePengambilan

                val orderCakeAdapter = HomeOrderUserCakeAdapter()
                rvOrderCakeUser.adapter = orderCakeAdapter
                rvOrderCakeUser.layoutManager = LinearLayoutManager(itemView.context)

                orderCakeAdapter.submitList(order.cart)

                root.setOnClickListener {
                    onItemClick.invoke(order.orderId)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeOrderUserAdapter.HomeUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvOrderUserBinding.inflate(inflater, parent, false)
        return HomeUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeUserViewHolder, position: Int) {
        val cart = getItem(position)
        holder.bind(cart)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
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