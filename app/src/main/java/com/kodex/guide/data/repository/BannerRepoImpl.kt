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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerRepoImpl @Inject constructor(
    private val dao: BannerDao,
    private val firestore: FirebaseFirestore
) : BannerRepo {

    private val TAG = "BannerRepoImpl"
    private val bannersCollection = firestore.collection("banners")

    private var _banners = MutableStateFlow<List<Banner>>(emptyList())
    private var isLoading = false

    init {
        Log.d(TAG, "=== BannerRepoImpl инициализирован ===")
        // При инициализации загружаем кэш из Room
        loadCachedBanners()
        // Загружаем свежие баннеры из Firestore
        loadFromFirestore()
    }

    /**
     * Наблюдение за активными баннерами (Flow)
     * Мгновенно возвращает кэш из Room, затем обновляет из Firestore
     */
    override fun observeActiveBanners(): Flow<List<Banner>> {
        // Возвращаем текущее значение + подписываемся на обновления
        return _banners
    }

    /**
     * Загружает баннеры из Room кэша
     */
    private fun loadCachedBanners() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cached = dao.getActiveBanners().map { it.toBanner() }
                Log.d(TAG, "✓ Загружено из кэша Room: ${cached.size} баннеров")
                _banners.value = cached
            } catch (e: Exception) {
                Log.e(TAG, "✗ Ошибка загрузки кэша", e)
            }
        }
    }

    /**
     * Загрузка баннеров из Firestore с сохранением в Room
     */
    private fun loadFromFirestore() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🚀 Загрузка баннеров из Firestore...")
                val snapshot = bannersCollection
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val dtos = snapshot.documents.mapNotNull { doc ->
                    val dto = doc.toObject(BannerDTO::class.java)
                    // Важно: устанавливаем id из ID документа Firestore
                    val finalDto = dto?.copy(id = doc.id)
                    Log.d(TAG, "Firestore doc: id=${doc.id}, isActive=${finalDto?.isActive}, postKey=${finalDto?.postKey}")
                    finalDto
                }

                Log.d(TAG, "📥 Получено баннеров из Firestore: ${dtos.size}")

                // Сохраняем в Room
                if (dtos.isNotEmpty()) {
                    dao.deleteAllBanners()
                    dao.insertBanners(dtos)
                    Log.d(TAG, "✓ Кэш в Room обновлён: ${dtos.size} баннеров")
                    
                    // Обновляем UI
                    _banners.value = dtos.map { it.toBanner() }
                    Log.d(TAG, "✓ UI обновлён: ${_banners.value.size} баннеров")
                } else {
                    Log.d(TAG, "⚠ Нет баннеров в Firestore, показываем кэш из Room")
                    loadCachedBanners()
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Ошибка загрузки из Firestore: ${e.message}", e)
                // Показываем кэш из Room
                loadCachedBanners()
            }
        }
    }

    /**
     * Принудительная загрузка из Firestore (suspend)
     */
    override suspend fun loadBannersIfNeeded() {
        if (isLoading) {
            Log.d(TAG, "⏭️ Загрузка уже идёт, пропускаем")
            return
        }
        isLoading = true

        try {
            Log.d(TAG, "🚀 Suspend загрузка баннеров из Firestore...")
            val snapshot = bannersCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val dtos = snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(BannerDTO::class.java)
                // Важно: устанавливаем id из ID документа Firestore
                val finalDto = dto?.copy(id = doc.id)
                Log.d(TAG, "Firestore doc: id=${doc.id}, isActive=${finalDto?.isActive}, postKey=${finalDto?.postKey}")
                finalDto
            }

            Log.d(TAG, "📥 Получено баннеров из Firestore: ${dtos.size}")

            // Сохраняем в Room
            if (dtos.isNotEmpty()) {
                dao.deleteAllBanners()
                dao.insertBanners(dtos)
                Log.d(TAG, "✓ Кэш в Room обновлён: ${dtos.size} баннеров")
                
                // Обновляем UI
                _banners.value = dtos.map { it.toBanner() }
                Log.d(TAG, "✓ UI обновлён: ${_banners.value.size} баннеров")
            } else {
                Log.d(TAG, "⚠ Нет баннеров в Firestore, показываем кэш из Room")
                loadCachedBanners()
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Ошибка загрузки из Firestore: ${e.message}", e)
            // Показываем кэш из Room
            loadCachedBanners()
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
        runCatching {
            Log.d(TAG, "Удаление баннера: id=$id")

            // 1. Удаляем из Firestore
            bannersCollection.document(id).delete().await()
            Log.d(TAG, "✓ Баннер удалён из Firestore")

            // 2. Удаляем из Room
            val deletedCount = dao.deleteBanner(id)
            Log.d(TAG, "✓ Баннер удалён из Room: $deletedCount записей")

            // 3. Обновляем список в UI
            _banners.value = _banners.value.filter { it.id != id }
            Log.d(TAG, "✓ Баннер удалён из UI")
            
            Unit
        }.onFailure { error ->
            Log.e(TAG, "✗ Ошибка удаления баннера", error)
        }
    }

}
