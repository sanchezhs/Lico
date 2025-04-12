package com.app.lico.data.db.entities

import com.app.lico.models.ShoppingItem

fun ShoppingItemEntity.toDomain(): ShoppingItem = ShoppingItem(
    id = id,
    name = name,
    quantity = quantity,
    unit = unit,
    isPurchased = isPurchased
)

fun ShoppingItem.toEntity(listId: Long): ShoppingItemEntity = ShoppingItemEntity(
    id = id,
    name = name,
    quantity = quantity,
    unit = unit,
    isPurchased = isPurchased,
    listId = listId
)
