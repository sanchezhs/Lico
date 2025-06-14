package com.app.lico.data.db.entities

import com.app.lico.models.ListItem

fun ListItemEntity.toDomain(): ListItem = ListItem(
    id = id,
    name = name,
    quantity = quantity,
    unit = unit,
    isChecked = isChecked,
    position = position,
)

fun ListItem.toEntity(listId: Long): ListItemEntity = ListItemEntity(
    id = id,
    name = name,
    quantity = quantity,
    unit = unit,
    isChecked = isChecked,
    listId = listId,
    position = position,
)
