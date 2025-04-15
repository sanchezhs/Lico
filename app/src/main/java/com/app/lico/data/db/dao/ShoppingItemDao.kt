package com.app.lico.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.lico.data.db.entities.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity)

    @Query("SELECT * FROM shopping_items WHERE listId = :listId")
    suspend fun getItemsByList(listId: Long): List<ShoppingItemEntity>

    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY position ASC")
    fun getItemsForListFlow(listId: Long): Flow<List<ShoppingItemEntity>>

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)

    @Query("SELECT * FROM shopping_items WHERE listId = :listId")
    suspend fun getItemsForList(listId: Long): List<ShoppingItemEntity>

    @Query("SELECT * FROM shopping_items")
    fun getAllItemsFlow(): Flow<List<ShoppingItemEntity>>
}
