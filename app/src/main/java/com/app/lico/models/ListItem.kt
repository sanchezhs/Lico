package com.app.lico.models

data class ListItem(
    val id: Long,
    var name: String,
    var quantity: Double? = null,
    var unit: String? = null,
    var isChecked: Boolean,
    val position: Int
)