package com.dissy.lizkitchen.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dissy.lizkitchen.databinding.RvCartBinding
import com.dissy.lizkitchen.model.Cart

class HomeCartUserAdapter(private val listener: CartInteractionListener) :
    androidx.recyclerview.widget.ListAdapter<Cart, HomeCartUserAdapter.CartUserViewHolder>(
        DiffCallback()
    ) {
    private var totalHarga: Long = 0
    interface CartInteractionListener {
        fun onQuantityChanged(cart: Cart)
    }
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
                Glide.with(itemView.context)
                    .load(cart.cake.imageUrl)
                    .into(ivCakeBanner)
                btnPlus.setOnClickListener {
                    cart.jumlahPesanan++
                    tvJmlh.text = cart.jumlahPesanan.toString()
                    listener.onQuantityChanged(cart)
                }
                btnMinus.setOnClickListener {
                    if (cart.jumlahPesanan > 1) {
                        cart.jumlahPesanan--
                        tvJmlh.text = cart.jumlahPesanan.toString()
                        listener.onQuantityChanged(cart)
                    }
                }
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