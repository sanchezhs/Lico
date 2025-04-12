package com.app.lico.data.di

import android.content.Context
import androidx.room.Room
import com.app.lico.data.db.AppDatabase
import com.app.lico.data.db.dao.ShoppingItemDao
import com.app.lico.data.db.dao.ShoppingListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shopping_db"
        ).build()
    }

    @Provides
    fun provideShoppingItemDao(db: AppDatabase): ShoppingItemDao = db.shoppingItemDao()

    @Provides
    fun provideShoppingListDao(db: AppDatabase): ShoppingListDao = db.shoppingListDao()
}
