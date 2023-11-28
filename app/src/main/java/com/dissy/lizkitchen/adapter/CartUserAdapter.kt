package com.dissy.lizkitchen.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dissy.lizkitchen.databinding.RvCartBinding
import com.dissy.lizkitchen.model.Cake
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.utility.convertStringToLong

class CartUserAdapter :
    androidx.recyclerview.widget.ListAdapter<Cart, CartUserAdapter.CartUserViewHolder>(
        DiffCallback()
    ) {
    private val cakesList = mutableListOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvCartBinding.inflate(inflater, parent, false)
        return CartUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartUserViewHolder, position: Int) {
        val cart = getItem(position)
        holder.bind(cart)
    }

    inner class CartUserViewHolder(
        private val binding: RvCartBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: Cart) {
            binding.apply {
                tvCakeName.text = cart.cake.namaKue
                tvPrice.text = cart.cake.harga
                tvJmlh.text = cart.jumlahPesanan.toString()
                val price = cart.cake.harga
                val cleanPrice = price.replace(".", "")
                val sumPrice = (cleanPrice.toLong() * cart.jumlahPesanan).toString()
                tvPriceSum.text = formatAndDisplayCurrency(sumPrice)
                Log.d("Price", formatAndDisplayCurrency(sumPrice))
                Glide.with(itemView.context)
                    .load(cart.cake.imageUrl)
                    .into(ivCakeBanner)
            }
        }
    }
    private class DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.cakeId  == newItem.cakeId
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }

    fun getTotalPrice(): Long {
        var total = 0L
        for (i in 0 until itemCount) {
            total += getItem(i).cake.harga.replace(".", "").toLong() * getItem(i).jumlahPesanan
        }
        return total
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