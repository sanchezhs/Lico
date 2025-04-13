package com.app.lico.data.mercadona

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM mercadona WHERE name LIKE '%' || :query || '%' LIMIT 10")
    suspend fun searchByName(query: String): List<ProductEntity>

    @Query("SELECT * FROM mercadona WHERE category = :category LIMIT 10")
    suspend fun getByCategory(category: String): List<ProductEntity>
}
