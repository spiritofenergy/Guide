package com.kodex.guide.ui.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.domain.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBooks(books: List<Book>)

    @Query("SELECT * FROM books")
    fun getAllPosts(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE authorUid = :uid ORDER BY timeStamp DESC")
    fun observeMyPosts(uid: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE `key` = :key")
    suspend fun getPostByKey(key: String): Book?

    @Query("UPDATE books SET isUploaded = :uploaded WHERE `key` = :key")
    suspend fun setUploaded(key: String, uploaded: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE `key` = :key)")
    suspend fun existsByKey(key: String): Boolean

    @Query("DELETE FROM books WHERE `key` = :key")
    suspend fun deleteByKey(key: String): Int

    // --- Методы для баннеров ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity)

    @Query("SELECT * FROM banners ORDER BY bannerPublishDate DESC")
    fun getAllActiveBannersFlow(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners ORDER BY bannerPublishDate DESC")
    suspend fun getActiveBanners(): List<BannerEntity>

    @Query("DELETE FROM banners WHERE `key` = :key")
    suspend fun deleteBannerByKey(key: String)

    @Query("DELETE FROM banners")
    suspend fun deleteAllBanners()

    @Transaction
    suspend fun insertPostWithBanner(post: Book, banner: BannerEntity) {
        insertPost(post)
        insertBanners(listOf(banner))
    }

    @Transaction
    suspend fun deletePostAndAssociatedBanner(key: String) {
        deleteByKey(key)
        deleteBannerByKey(key)
    }
    @Transaction
    suspend fun replaceAllBanners(banners: List<BannerEntity>) {
        deleteAllBanners()
        insertBanners(banners)
    }
}