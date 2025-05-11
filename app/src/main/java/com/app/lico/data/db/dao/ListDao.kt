package com.app.lico.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.lico.data.db.entities.ListItemEntity
import com.app.lico.data.db.entities.ListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ListEntity): Long

    @Query("SELECT * FROM lists")
    fun getAllLists(): Flow<List<ListEntity>>

    @Query("SELECT * FROM list_items WHERE listId = :listId ORDER BY position ASC")
    fun getItemsForListFlow(listId: Long): Flow<List<ListItemEntity>>

    @Query("SELECT * FROM lists WHERE id = :listId")
    fun getListById(listId: Long): Flow<ListEntity?>

    @Delete
    suspend fun deleteList(list: ListEntity)

    @Query("UPDATE lists SET sortOption = :sortOption WHERE id = :listId")
    suspend fun updateSortOption(listId: Long, sortOption: String)

}
