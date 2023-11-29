package com.dissy.lizkitchen.model

data class Cake @JvmOverloads constructor(
    val documentId: String = "",
    val harga: String = "",
    val imageUrl: String = "",
    val namaKue: String = "",
    val stok: Long = 0,
)