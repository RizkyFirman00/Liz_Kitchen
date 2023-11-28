package com.dissy.lizkitchen.model

data class Order(
    val orderId: String,
    val cakeId: String,
    val cake: Cake,
    val jumlahPesanan: String,
)