package com.kodex.guide.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.kodex.guide.data.repository.AiDescriptionRepoImpl
import com.kodex.guide.data.repository.BannerRepoImpl
import com.kodex.guide.domain.repository.AiDescriptionRepo
import com.kodex.guide.domain.repository.BannerRepo
import com.kodex.guide.ui.db.BannerDao
import com.kodex.guide.ui.db.DraftDao
import com.kodex.guide.ui.db.MainDb
import com.kodex.guide.ui.db.RoomDao
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
    fun provideAiDescriptionRepo(app: Application): AiDescriptionRepo {
        return AiDescriptionRepoImpl(app)
    }

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

    @Provides
    @Singleton
    fun provideBannerDao(database: MainDb): BannerDao {
        return database.bannerDao()
    }

    @Provides
    @Singleton
    fun provideBannerRepo(
        dao: BannerDao,
        firestore: FirebaseFirestore
    ): BannerRepo {
        return BannerRepoImpl(dao, firestore)
    }
}

