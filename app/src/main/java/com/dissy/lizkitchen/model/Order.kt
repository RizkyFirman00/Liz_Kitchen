package com.dissy.lizkitchen.model

data class Order (
    val cart: List<Cart> = listOf(),
    val orderId: String = "",
    val status: String = "",
    val metodePengambilan: String = "",
    val tanggalOrder: String = "",
    val totalPrice: Long = 0,
    val user: User = User(),
)