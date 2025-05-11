package com.app.lico.models

enum class SortOption {
    DEFAULT, NAME, QUANTITY
}

data class Lists(
    val id: Long,
    var name: String,
    var typeId: String,
    val items: List<ListItem>,
    val sortOption: SortOption = SortOption.DEFAULT
)