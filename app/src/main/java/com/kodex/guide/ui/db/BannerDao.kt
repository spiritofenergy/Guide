package com.kodex.guide.ui.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с баннерами в Room
 */
@Dao
interface BannerDao {
    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActiveBanners(): Flow<List<BannerDTO>>

    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY createdAt DESC")
    suspend fun getActiveBanners(): List<BannerDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerDTO): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerDTO>)

    @Query("DELETE FROM banners")
    suspend fun deleteAllBanners()

    @Query("UPDATE banners SET isActive = 0 WHERE id = :id")
    suspend fun deactivateBanner(id: String)

    @Query("DELETE FROM banners WHERE id = :id")
    suspend fun deleteBanner(id: String)
}
