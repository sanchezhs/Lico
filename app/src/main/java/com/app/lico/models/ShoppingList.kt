package com.app.lico.models

enum class SortOption {
    DEFAULT, NAME, QUANTITY
}

data class ShoppingList(
    val id: Long,
    var name: String,
    val items: MutableList<ShoppingItem>,
    val sortOption: SortOption = SortOption.DEFAULT
)