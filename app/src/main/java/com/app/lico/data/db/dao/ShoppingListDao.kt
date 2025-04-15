package com.app.lico.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.lico.data.db.entities.ShoppingItemEntity
import com.app.lico.data.db.entities.ShoppingListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity): Long

    @Query("SELECT * FROM shopping_lists")
    fun getAllLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY position ASC")
    fun getItemsForListFlow(listId: Long): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_lists WHERE id = :listId")
    fun getListById(listId: Long): Flow<ShoppingListEntity?>

    @Delete
    suspend fun deleteList(list: ShoppingListEntity)

    @Query("UPDATE shopping_lists SET sortOption = :sortOption WHERE id = :listId")
    suspend fun updateSortOption(listId: Long, sortOption: String)

}
