package com.dissy.lizkitchen.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dissy.lizkitchen.databinding.RvAdminBinding
import com.dissy.lizkitchen.model.Cake

class HomeAdminAdapter(private val onItemClick: (String) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Cake, HomeAdminAdapter.HomeAdminViewHolder>(
        DiffCallback()
    ) {
    private val cakesList = mutableListOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvAdminBinding.inflate(inflater, parent, false)
        return HomeAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeAdminViewHolder, position: Int) {
        val indekos = getItem(position)
        holder.bind(indekos, onItemClick)
    }

    inner class HomeAdminViewHolder(
        private val binding: RvAdminBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cake: Cake, onItemClick: (String) -> Unit) {
            binding.apply {
                tvCakeName.text = cake.namaKue
                tvPrice.text = cake.harga
                tvStok.text = cake.stok
                Glide.with(itemView.context)
                    .load(cake.imageUrl)
                    .into(ivCakeBanner)
                root.setOnClickListener {
                    onItemClick.invoke(cake.namaKue)
                }
            }
        }
    }

    fun updateData(newData: List<String>) {
        cakesList.clear()
        cakesList.addAll(newData)
        if (cakesList.isNotEmpty()) {
            notifyDataSetChanged()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Cake>() {
        override fun areItemsTheSame(oldItem: Cake, newItem: Cake): Boolean {
            return oldItem.namaKue == newItem.namaKue
        }

        override fun areContentsTheSame(oldItem: Cake, newItem: Cake): Boolean {
            return oldItem == newItem
        }
    }
}