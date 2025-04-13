package com.app.lico.models

data class ShoppingItem(
    val id: Long,
    var name: String,
    var quantity: Double,
    var unit: String,
    var isPurchased: Boolean,
    val position: Int
)