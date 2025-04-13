package com.app.lico.data.mercadona

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class], version = 1)
abstract class MercadonaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        fun create(context: Context): MercadonaDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MercadonaDatabase::class.java,
                "mercadona.db"
            )
                .createFromAsset("mercadona.db")
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
