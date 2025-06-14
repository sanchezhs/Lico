package com.app.lico.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_types")
data class ListTypeEntity(
    @PrimaryKey val id: String,  // e.g., "shopping", "todo"
    val displayName: String,     // e.g., "Compras", "Tareas"
    val iconName: String,        // e.g., "shopping_cart"
    val colorHex: String,        // e.g., "#FF9800"
    val showQuantity: Boolean
)
