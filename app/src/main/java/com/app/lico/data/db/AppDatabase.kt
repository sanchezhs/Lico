package com.app.lico.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.lico.data.db.dao.ShoppingItemDao
import com.app.lico.data.db.dao.ShoppingListDao
import com.app.lico.data.db.entities.ShoppingItemEntity
import com.app.lico.data.db.entities.ShoppingListEntity

@Database(
    entities = [ShoppingListEntity::class, ShoppingItemEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun shoppingListDao(): ShoppingListDao
}