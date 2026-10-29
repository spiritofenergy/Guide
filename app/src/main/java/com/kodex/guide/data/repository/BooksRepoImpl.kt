package com.kodex.guide.data.repository

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kodex.guide.data.images.BitmapEncoder
import com.kodex.guide.data.mapper.toBannerEntity
import com.kodex.guide.data.mapper.toBookDTO
import com.kodex.guide.data.mapper.toDTO
import com.kodex.guide.data.mapper.toDomain
import com.kodex.guide.data.mapper.toEntity
import com.kodex.guide.data.mapper.toRatingData
import com.kodex.guide.data.model.toBannerEntity
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.di.MainModule
import com.kodex.guide.domain.model.Banner
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.domain.repository.BooksRepo
import com.kodex.guide.presentation.home.BookFactoryPaging
import com.kodex.guide.ui.db.RoomDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

const val BASE_64 = true

class BooksRepo_Impl @Inject constructor(
    private val dataSource: FirebaseBooksDataSource,
    private val dao: RoomDao,
    //private val storageDataSource: FireBaseStorageDataSource,
    private val bitmapEncoder: BitmapEncoder,
    @MainModule.ApplicationScope private val externalScope: CoroutineScope // 👈 Добавили аннотацию

) : BooksRepo {


    init {
        // ✅ Запускаем синхронизацию ОДИН РАЗ при создании репозитория.
        // Она будет идти в фоне, пока UI спокойно читает Room.
        externalScope.launch {
            syncBannersFromFirebase()
        }
    }
    override fun getBooks(
        favsKeysList: List<String>,
        bookFilter: BookFilterState
    ): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = 16,
                prefetchDistance = 3,
                initialLoadSize = 48,
            ),
            pagingSourceFactory = {
                BookFactoryPaging(
                    dataSource,
                    bookFilter,
                    dao
                )
            }
        ).flow
    }

    override suspend fun saveBannersToLocalDb(banner: Banner): Result<Unit> {
        return try {
            dao.insertBanners(listOf(banner.toEntity()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun publishPostAndBanner(book: Book, uri: Uri?): Result<Unit> {
        return try {
            val imageUrl = if (uri != null) bitmapEncoder.imageToBase64(uri) else book.imageUrl
            val preparedBook = book.copy(imageUrl = imageUrl)

            // 1. Сохраняем пост в Firebase
            val postResult = dataSource.saveBook(preparedBook.toBookDTO())
            if (postResult.isFailure) return Result.failure(postResult.exceptionOrNull()!!)

            // 2. Сохраняем пост в Room (если нужно, добавьте dao.insertPost(preparedBook))

            // 3. СОЗДАЕМ И СОХРАНЯЕМ БАННЕР через единую функцию
            val bannerEntity = preparedBook.toBannerEntity()
            dao.insertBanner(bannerEntity)

            // 4. Сохраняем в Firebase (отдельная коллекция)
            val bannerResult =
                dataSource.saveBannerToFirebase(preparedBook.copy(isBanner = true).toBookDTO())
            if (bannerResult.isFailure) {
                // Логируем ошибку, но пост и локальный баннер уже сохранены
            }
            // В самом конце метода publishPostAndBanner:
            val bannerDto = preparedBook.copy(
                isBanner = true,
                active = true // <--- Важно добавить, чтобы фильтр в Firebase работал!
            ).toBookDTO()

            dataSource.saveBannerToFirebase(bannerDto)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBook(book: Book): Result<Unit> {
        return dataSource.deleteBook(book.toBookDTO())
    }

    // only BASE64 image
    override suspend fun saveBook(book: Book, uri: Uri?): Result<Unit> {
        val imageUrl = bitmapEncoder.imageToBase64(uri)
        return dataSource.saveBook(book.copy(imageUrl = imageUrl).toBookDTO())
    }

    override suspend fun submitUserRating(
        ratingData: RatingData,
        bookId: String
    ): Result<Unit> {
        return dataSource.submitUserRating(ratingData.toDTO(), bookId)
    }

    override suspend fun deleteComment(uid: String): Result<Unit> {
        return dataSource.deleteComment(uid)
    }

    /* override fun getBannersFlow(): Flow<List<Banner>> {
        return dao.getAllActiveBannersFlow().map { entities ->
            entities.map { it.toDomain() } // Конвертируем Entity в чистую модель Banner
        }
    }*/
    // В BooksRepo_Impl.kt
    override fun getBannersFlow(): Flow<List<Banner>> {
        return dao.getAllActiveBannersFlow()
            .map { entities -> entities.map { it.toDomain() } }
    }

    private suspend fun syncBannersFromFirebase() {
        dataSource.deleteExpiredBanners()
        val result = dataSource.getActiveBannersFromFirebase()
        if (result.isSuccess) {
            val remoteBanners = result.getOrNull() ?: return
            val entities: List<BannerEntity> = remoteBanners.map { dto ->
                dto.toBannerEntity()
            }
            // 3. Обновляем Room одной транзакцией (без моргания)
            dao.replaceAllBanners(entities)
            //  dao.insertBanners(entities)  // ← Просто entities, без listOf()
        }
    }
    /*
        private suspend fun syncBannersFromFirebase() {
            // 1. Сначала удаляем просроченные баннеры прямо из Firebase
            dataSource.deleteExpiredBanners()
            val result = dataSource.getActiveBannersFromFirebase()
            if (result.isSuccess) {
                val remoteBanners = result.getOrNull()
                if (remoteBanners != null) {
                    Log.d("BannerSync", "✅ Получено ${remoteBanners.size} баннеров из Firebase")
                    // 1. Очищаем старые баннеры
                    dao.deleteAllBanners()
                    // 3. Конвертируем BookDTO в BannerEntity с помощью НОВОГО маппера
                    val entities: List<BannerEntity> = remoteBanners.map { dto ->
                        dto.toBannerEntity() }
                    // 3. Сохраняем ВЕСЬ СПИСОК ОДНИМ ВЫЗОВОМ
                    dao.insertBanners(entities)  // ← Просто entities, без listOf()
                    Log.d("BannerSync", "💾 Успешно сохранено ${entities.size} баннеров в Room")
                }
            }
        }*/

    override suspend fun getBookComments(bookId: String): Result<List<RatingData>> {
        return dataSource.getBookComments(bookId).map { list ->
            list.map { rData ->
                rData.toRatingData()
            }
        }
    }

    override suspend fun getUserRating(bookId: String): Result<RatingData?> {
        return dataSource.getUserRating(bookId).map {
            it?.toRatingData()
        }
    }
}