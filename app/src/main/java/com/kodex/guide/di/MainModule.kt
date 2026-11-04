package com.kodex.guide.di

import  android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.data.source.remote.FirebaseFavoritesDataSource
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.data.source.remote.FirebaseAuthDataSource
import com.kodex.guide.data.source.remote.FirebaseModerationDataSource
import com.kodex.guide.data.source.remote.FirebaseUserSettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object   MainModule {


    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
    @Provides
    @Singleton

    fun provideFirebaseDataSource (
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): FirebaseBooksDataSource{
        return FirebaseBooksDataSource(db, auth)
    }

    @Provides
    @Singleton
    fun provideFavoritesDataSource (
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): FirebaseFavoritesDataSource{
        return FirebaseFavoritesDataSource(db, auth)
    }

    @Provides
    @Singleton
    fun provideUserSettingsDataSource (
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): FirebaseUserSettingsDataSource {
        return FirebaseUserSettingsDataSource(db, auth)
    }

    @Provides
    @Singleton
    fun provideModerationDataSource (
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): FirebaseModerationDataSource {
        return FirebaseModerationDataSource(db, auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return Firebase.auth
    }
    @Provides
    @Singleton
    fun provideFirebaseStore(): FirebaseFirestore{
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideAuthManager(
        auth: FirebaseAuth
    ): FirebaseAuthDataSource{
        return FirebaseAuthDataSource(auth)
    }

    @Provides
    @Singleton
    fun provideStoreManager(
        app: Application
    ):PreferenceDataSource{
        return PreferenceDataSource(app)
    }


    // 1. Создаем аннотацию, чтобы отделить этот scope от других
    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationScope

    // 2. Создаем модуль для Hilt
    @Module
    @InstallIn(SingletonComponent::class)
    object CoroutinesModule {

        @Provides
        @Singleton
        @ApplicationScope // 👈 Привязываем аннотацию
        fun provideApplicationScope(): CoroutineScope {
            // SupervisorJob нужен, чтобы падение одной корутины не убивало весь scope
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }
    }
}