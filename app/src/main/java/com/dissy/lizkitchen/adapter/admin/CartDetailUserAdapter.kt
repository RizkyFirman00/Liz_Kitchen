package com.dissy.lizkitchen.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dissy.lizkitchen.databinding.RvOrderBinding
import com.dissy.lizkitchen.model.Cart
import com.dissy.lizkitchen.model.Order

class CartDetailUserAdapter() :
    ListAdapter<Cart, CartDetailUserAdapter.CartDetailUserViewHolder>(
        DiffCallback()
    ) {
    inner class CartDetailUserViewHolder(private val binding: RvOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.apply {
                tvCakeName.text = cart.cake.namaKue
                tvJumlahPesanan.text = cart.jumlahPesanan.toString()
                Glide.with(itemView.context)
                    .load(cart.cake.imageUrl)
                    .into(ivImageCake)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartDetailUserAdapter.CartDetailUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvOrderBinding.inflate(inflater, parent, false)
        return CartDetailUserViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartDetailUserViewHolder,
        position: Int
    ) {
        val order = getItem(position)
        holder.bind(order)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.cake.namaKue == newItem.cake.namaKue
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }
}