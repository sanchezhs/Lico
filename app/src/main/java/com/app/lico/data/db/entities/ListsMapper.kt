package com.app.lico.data.db.entities

import com.app.lico.models.ListItem
import com.app.lico.models.Lists
import com.app.lico.models.SortOption


fun ListEntity.toDomain(items: List<ListItem>) = Lists(
    id = id,
    name = name,
    typeId = typeId,
    items = items.toMutableList(),
    sortOption = SortOption.valueOf(sortOption)
)

fun Lists.toEntity() = ListEntity(
    id = id,
    name = name,
    typeId = typeId,
    sortOption = sortOption.name
)
