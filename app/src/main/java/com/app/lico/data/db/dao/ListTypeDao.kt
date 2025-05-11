package com.app.lico.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.lico.data.db.entities.ListTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListTypeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(types: List<ListTypeEntity>)

    @Query("SELECT * FROM list_types")
    fun getAllListsTypes(): Flow<List<ListTypeEntity>>
}
