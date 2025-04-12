package com.app.lico.models

data class ShoppingList(
    val id: Long,
    var name: String,
    val items: MutableList<ShoppingItem>
)