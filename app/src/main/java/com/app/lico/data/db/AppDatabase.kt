package com.app.lico.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.lico.data.db.dao.ListItemDao
import com.app.lico.data.db.dao.ListDao
import com.app.lico.data.db.dao.ListTypeDao
import com.app.lico.data.db.entities.ListItemEntity
import com.app.lico.data.db.entities.ListEntity
import com.app.lico.data.db.entities.ListTypeEntity

@Database(
    entities = [ListEntity::class, ListItemEntity::class, ListTypeEntity::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listItemDao(): ListItemDao
    abstract fun listsDao(): ListDao
    abstract fun listTypeDao(): ListTypeDao
}