package com.app.lico.data.di

import android.content.Context
import androidx.room.Room
import com.app.lico.data.mercadona.MercadonaDatabase
import com.app.lico.data.mercadona.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MercadonaModule {

    @Provides
    @Singleton
    fun provideMercadonaDatabase(@ApplicationContext context: Context): MercadonaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MercadonaDatabase::class.java,
            "mercadona.db"
        )
            .createFromAsset("mercadona.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideProductDao(db: MercadonaDatabase): ProductDao {
        return db.productDao()
    }
}
