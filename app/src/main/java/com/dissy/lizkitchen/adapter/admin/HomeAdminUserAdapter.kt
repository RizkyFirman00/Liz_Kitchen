package com.dissy.lizkitchen.adapter.admin

import com.dissy.lizkitchen.model.Order

class HomeAdminUserAdapter(private val onItemClick: (String) -> Unit) : androidx.recyclerview.widget.ListAdapter<Order, HomeAdminUserAdapter.HomeAdminViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): HomeAdminViewHolder {
        val inflater = android.view.LayoutInflater.from(parent.context)
        val binding = com.dissy.lizkitchen.databinding.RvUserBinding.inflate(inflater, parent, false)
        return HomeAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeAdminViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order, onItemClick)
    }

    inner class HomeAdminViewHolder(private val binding: com.dissy.lizkitchen.databinding.RvUserBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order, onItemClick: (String) -> Unit) {
            if (order.status == "Selesai") {
                binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
            } else if (order.status == "Dibatalkan") {
                binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
            } else if (order.status == "Menunggu Pembayaran") {
                binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#D10826"))
            } else if (order.status == "Sedang Dikirim") {
                binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
            } else if (order.status == "Sedang Diproses") {
                binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#9C6843"))
            } else if (order.status == "Sudah Dikonfirmasi") {
                binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#0ACB12"))
            }
            binding.apply {
                tvOrderId.text = order.orderId
                tvOrderStatus.text = order.status
                tvUsername.text = order.user.username
                tvPhoneNumber.text = order.user.phoneNumber
                root.setOnClickListener {
                    onItemClick.invoke(order.orderId)
                }
            }
        }
    }

    private class DiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem:Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}