package com.kodex.guide.domain.repository

import com.kodex.guide.domain.model.Banner
import kotlinx.coroutines.flow.Flow

/**
 * Repository для работы с баннерами
 */
interface BannerRepo {
    fun observeActiveBanners(): Flow<List<Banner>>
    suspend fun loadBannersIfNeeded()
    suspend fun getActiveBanners(): List<Banner>
    suspend fun saveBanner(banner: Banner): Result<Unit>
    suspend fun deleteBanner(id: String): Result<Unit>
}
