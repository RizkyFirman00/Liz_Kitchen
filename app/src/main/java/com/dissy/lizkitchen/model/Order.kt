package com.dissy.lizkitchen.model

data class Order(
    val orderId: String,
    val cart: Cart,
    val jumlahPesanan: String,
)