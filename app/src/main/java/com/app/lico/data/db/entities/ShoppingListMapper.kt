package com.app.lico.data.db.entities

import com.app.lico.models.ShoppingItem
import com.app.lico.models.ShoppingList


fun ShoppingListEntity.toDomain(items: List<ShoppingItem>) = ShoppingList(
    id = id,
    name = name,
    items = items.toMutableList()
)

fun ShoppingList.toEntity() = ShoppingListEntity(
    id = id,
    name = name
)
