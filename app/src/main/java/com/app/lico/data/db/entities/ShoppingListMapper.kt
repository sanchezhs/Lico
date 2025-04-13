package com.app.lico.data.db.entities

import com.app.lico.models.ShoppingItem
import com.app.lico.models.ShoppingList
import com.app.lico.models.SortOption


fun ShoppingListEntity.toDomain(items: List<ShoppingItem>) = ShoppingList(
    id = id,
    name = name,
    items = items.toMutableList(),
    sortOption = SortOption.valueOf(sortOption)
)

fun ShoppingList.toEntity() = ShoppingListEntity(
    id = id,
    name = name,
    sortOption = sortOption.name
)
