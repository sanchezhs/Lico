package com.app.lico.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.lico.data.db.AppDatabase
import com.app.lico.data.db.dao.ListItemDao
import com.app.lico.data.db.dao.ListDao
import com.app.lico.data.db.dao.ListTypeDao
import com.app.lico.data.db.entities.ListTypeEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "list_db"
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val types = listOf(
                            ListTypeEntity(id = "shopping", displayName = "Compras", iconName = "shopping_cart", colorHex = "#A5C2EE", showQuantity = true),
                            ListTypeEntity(id = "todo", displayName = "Tareas", iconName = "check_circle", colorHex = "#1C3A54", showQuantity = false),
                            ListTypeEntity(id = "books", displayName = "Libros", iconName = "book", colorHex = "#7A99C8", showQuantity = false),
                            ListTypeEntity(id = "travel", displayName = "Viaje", iconName = "flight", colorHex = "#34577D", showQuantity = false),
                            ListTypeEntity(id = "custom", displayName = "Otros", iconName = "list", colorHex = "#C6B08D", showQuantity = false)
                        )
                        provideListTypeDao(provideDatabase(context)).insertAll(types)
                    }
                }
            })
            .build()
    }

    @Provides fun provideListItemDao(db: AppDatabase): ListItemDao = db.listItemDao()
    @Provides fun provideListsDao(db: AppDatabase): ListDao = db.listsDao()
    @Provides fun provideListTypeDao(db: AppDatabase): ListTypeDao = db.listTypeDao()
}
