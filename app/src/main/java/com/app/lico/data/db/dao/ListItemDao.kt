package com.app.lico.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.lico.data.db.entities.ListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ListItemEntity)

    @Query("SELECT * FROM list_items WHERE listId = :listId")
    suspend fun getItemsByList(listId: Long): List<ListItemEntity>

    @Query("SELECT * FROM list_items WHERE listId = :listId ORDER BY position ASC")
    fun getItemsForListFlow(listId: Long): Flow<List<ListItemEntity>>

    @Delete
    suspend fun deleteItem(item: ListItemEntity)

    @Query("SELECT * FROM list_items WHERE listId = :listId")
    suspend fun getItemsForList(listId: Long): List<ListItemEntity>

    @Query("SELECT * FROM list_items")
    fun getAllItemsFlow(): Flow<List<ListItemEntity>>
}
