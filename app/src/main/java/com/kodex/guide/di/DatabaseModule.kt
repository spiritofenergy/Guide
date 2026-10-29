package com.kodex.guide.di

import android.content.Context
import androidx.room.Room
import com.kodex.guide.ui.db.DraftDao
import com.kodex.guide.ui.db.MainDb
import com.kodex.guide.ui.db.RoomDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.intellij.lang.annotations.MagicConstant
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): MainDb {
        return Room.databaseBuilder(
            context,
            MainDb::class.java,
            "books"
        )   .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun provideDraftDao(database: MainDb): DraftDao {
        return database.draftDao()
    }

    @Provides
    @Singleton
    fun providePostDao(database: MainDb): RoomDao {
        return database.trackDao()
    }
}

