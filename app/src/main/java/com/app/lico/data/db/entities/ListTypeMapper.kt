package com.app.lico.data.db.entities

import com.app.lico.models.ListItem
import com.app.lico.models.ListType
import com.app.lico.models.Lists


fun ListTypeEntity.toDomain(): ListType =
    ListType(
        id = id,
        displayName = displayName,
        iconName = iconName,
        colorHex = colorHex,
        showQuantity = showQuantity
)

fun ListEntity.toDomain(
    items: List<ListItem>,
    type: ListType?
): Lists = Lists(
    id = id,
    name = name,
    typeId = typeId,
    items = items
)
