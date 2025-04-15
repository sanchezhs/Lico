package com.app.lico.data.mercadona

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mercadona")
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "category")
    val category: String?,

    @ColumnInfo(name = "supermarket")
    val supermarket: String?,
)
