package com.kodex.guide.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kodex.guide.data.mapper.toBanner
import com.kodex.guide.data.mapper.toBannerDTO
import com.kodex.guide.domain.model.Banner
import com.kodex.guide.domain.repository.BannerRepo
import com.kodex.guide.ui.db.BannerDao
import com.kodex.guide.ui.db.BannerDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerRepoImpl @Inject constructor(
    private val dao: BannerDao,
    private val firestore: FirebaseFirestore
) : BannerRepo {

    private val TAG = "BannerRepoImpl"
    private val bannersCollection = firestore.collection("banners")
    
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    private var isLoading = false

    override fun observeActiveBanners(): Flow<List<Banner>> = _banners

    override suspend fun loadBannersIfNeeded() {
        if (isLoading) return
        isLoading = true
        
        try {
            Log.d(TAG, "Загрузка баннеров из Firestore...")
            val snapshot = bannersCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val dtos = snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(BannerDTO::class.java)
                if (dto?.isActive == true) dto else null
            }
            
            dao.deleteAllBanners()
            if (dtos.isNotEmpty()) {
                dao.insertBanners(dtos)
                Log.d(TAG, "Кэш в Room обновлён: ${dtos.size} баннеров")
                _banners.value = dtos.map { it.toBanner() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки баннеров из Firestore", e)
            // Показываем кэш из Room
            val cached = try {
                dao.getActiveBanners().map { it.toBanner() }
            } catch (ex: Exception) {
                emptyList()
            }
            _banners.value = cached
        } finally {
            isLoading = false
        }
    }

    override suspend fun getActiveBanners(): List<Banner> = withContext(Dispatchers.IO) {
        val dtos = dao.getActiveBanners()
        Log.d(TAG, "getActiveBanners: ${dtos.size} баннеров")
        dtos.map { it.toBanner() }
    }

    override suspend fun saveBanner(banner: Banner): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Log.d(TAG, "=== НАЧАЛО СОХРАНЕНИЯ БАННЕРА ===")
            Log.d(TAG, "ID баннера: ${banner.id}")
            Log.d(TAG, "Автор UID: ${banner.authorUid}")

            val dto = banner.toBannerDTO()
            Log.d(TAG, "DTO: id=${dto.id}, isActive=${dto.isActive}")
            
            Log.d(TAG, "Сохранение в Firestore...")
            bannersCollection.document(dto.id).set(dto).await()
            Log.d(TAG, "✓ Баннер сохранён в Firestore")
            
            Log.d(TAG, "Сохранение в Room...")
            dao.insertBanner(dto)
            Log.d(TAG, "✓ Баннер сохранён в Room")
            
            Log.d(TAG, "=== БАННЕР УСПЕШНО СОХРАНЁН ===")
            Unit
        }.onFailure { error ->
            Log.e(TAG, "✗ Ошибка сохранения баннера", error)
        }
    }

    override suspend fun deleteBanner(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            bannersCollection.document(id).delete().await()
            dao.deactivateBanner(id)
        }
    }
}
