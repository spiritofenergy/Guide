package com.kodex.guide.di

import com.kodex.guide.data.repository.BooksRepo_Impl
import com.kodex.guide.data.repository.FavoritesFirebaseRepoImpl
import com.kodex.guide.data.repository.FirebaseAuthRepoImpl
import com.kodex.guide.data.repository.ModerationRepoImpl
import com.kodex.guide.data.repository.MyPostsRepositoryImpl
import com.kodex.guide.data.repository.RelatedBooksRepositoryImpl
import com.kodex.guide.data.repository.SavedPostsLocalSourceImpl
import com.kodex.guide.data.repository.SavedPostsRepositoryImpl
import com.kodex.guide.data.repository.UserSettingsRepoImpl
import com.kodex.guide.data.repository.UserAccessRepoImpl
import com.kodex.guide.data.repository.UserRoleRepoImpl
import com.kodex.guide.data.source.local.SavedPostsLocalSource
import com.kodex.guide.domain.repository.AuthRepo
import com.kodex.guide.domain.repository.BooksRepo
import com.kodex.guide.domain.repository.FavoritesRepo
import com.kodex.guide.domain.repository.ModerationRepo
import com.kodex.guide.domain.repository.MyPostsRepo
import com.kodex.guide.domain.repository.RelatedBooksRepo
import com.kodex.guide.domain.repository.SavedPostsRepo
import com.kodex.guide.domain.repository.UserAccessRepo
import com.kodex.guide.domain.repository.UserRoleRepo
import com.kodex.guide.domain.repository.UserSettingsRepo
import com.kodex.guide.domain.role.DefaultRolePermissionChecker
import com.kodex.guide.domain.role.RolePermissionChecker
import com.kodex.guide.domain.tarif.AuthStateProvider
import com.kodex.guide.domain.tarif.DefaultTariffPolicy
import com.kodex.guide.domain.tarif.FirebaseAuthStateProvider
import com.kodex.guide.domain.tarif.TariffPolicy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.kodex.guide.data.images.ImagePreparer
import com.kodex.guide.data.images.ImagePreparerImpl
import com.kodex.guide.data.repository.YandexPaymentRepositoryImpl
import com.kodex.guide.domain.ai.FirebasePostDescriptionGenerator
import com.kodex.guide.domain.ai.PostDescriptionGenerator
import com.kodex.guide.domain.repository.PaymentRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        impl: YandexPaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindPostDescriptionGenerator(
        impl: FirebasePostDescriptionGenerator
    ): PostDescriptionGenerator

    @Binds
    @Singleton
    abstract fun bindImagePreparer(
        impl: ImagePreparerImpl
    ): ImagePreparer


    @Binds
    @Singleton
    abstract fun bindMyPostsRepo(impl: MyPostsRepositoryImpl): MyPostsRepo

    @Binds
    @Singleton
    abstract fun bindRelatedBooksRepository(
        impl: RelatedBooksRepositoryImpl
    ): RelatedBooksRepo

    @Binds
    @Singleton
    abstract fun bindSavedPostsLocalSource(
        impl: SavedPostsLocalSourceImpl
    ): SavedPostsLocalSource

    @Binds
    @Singleton
    abstract fun bindSavedPostsRepository(
        impl: SavedPostsRepositoryImpl
    ): SavedPostsRepo
    @Binds
    abstract fun bindUserAccessRepository(
        impl: UserAccessRepoImpl
    ): UserAccessRepo

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepoImpl
    ): AuthRepo

    @Binds
    abstract fun bindTariffPolicy(
        impl: DefaultTariffPolicy
    ): TariffPolicy

    @Binds
    abstract fun bindAuthStateProvider(
        impl: FirebaseAuthStateProvider
    ): AuthStateProvider

    @Binds
    abstract fun bindUserRoleRepository(
        impl: UserRoleRepoImpl
    ): UserRoleRepo

    @Binds
    abstract fun bindRolePermissionChecker(
        impl: DefaultRolePermissionChecker
    ): RolePermissionChecker

    @Binds
    @Singleton
    abstract fun bindBooksPepo(
        booksRepoImpl: BooksRepo_Impl
    ): BooksRepo

    @Binds
    @Singleton
    abstract fun bindModerationPepo(
        moderationRepoInfo: ModerationRepoImpl
    ): ModerationRepo

    @Binds
    @Singleton
    abstract fun bindFavoritesRepo(
        favoritesRepoImpl: FavoritesFirebaseRepoImpl
    ): FavoritesRepo

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepo(
        userSettingsRepo: UserSettingsRepoImpl
    ): UserSettingsRepo
}