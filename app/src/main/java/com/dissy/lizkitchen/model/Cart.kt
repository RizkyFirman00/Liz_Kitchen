package com.dissy.lizkitchen.model

data class Cart(
    val cakeId: String,
    val cake: Cake,
    val jumlahPesanan: Long,
)
