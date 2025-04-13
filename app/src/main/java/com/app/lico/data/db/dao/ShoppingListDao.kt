package com.app.lico.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.lico.data.db.entities.ShoppingListEntity

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity): Long

    @Query("SELECT * FROM shopping_lists")
    suspend fun getAllLists(): List<ShoppingListEntity>

    @Delete
    suspend fun deleteList(list: ShoppingListEntity)

    @Query("UPDATE shopping_lists SET sortOption = :sortOption WHERE id = :listId")
    suspend fun updateSortOption(listId: Long, sortOption: String)

}
