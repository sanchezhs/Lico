package com.app.lico.models

import com.app.lico.R

data class ListType(
    val id: String,
    val displayName: String,
    val iconName: String,
    val colorHex: String
)

fun ListType.toIconRes(): Int = when (iconName) {
    "shopping_cart" -> R.drawable.empty_shopping_cart
    "check_circle" -> R.drawable.check_circle
    "book" -> R.drawable.book
    "flight" -> R.drawable.flight
    "list" -> R.drawable.list
    else -> throw IllegalArgumentException("No icon found for $iconName")
}
