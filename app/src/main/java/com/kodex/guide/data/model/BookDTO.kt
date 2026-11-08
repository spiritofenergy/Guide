package com.kodex.guide.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.domain.model.BookCategories

data class BookDTO(
    val key: String = "",
    val title: String = "",
    val searchTitle: String = title.lowercase(),
    val description: String = "",
    val price: Int = 0,
    val telephone: String = "",
    val categoryIndex: Int = BookCategories.OTHER.id,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val isAuthor: Boolean = false,
    val authorRole: Int = 0,  // ✅ Роль автора (UserRole.level)
    val publishPeriod: Int = 1,
    val timeStamp: Long = System.currentTimeMillis(),
    val deleteDate: Long = 0L,
    val village: String = "",
    val street: String = "",
    val house: String = "",
    val flat: String = "",
    val location: Boolean = false,
    val delivery: Boolean = false,
    val payment: Boolean = false,
    val ratingsList: List<Int> = emptyList(),
    val latitude: Any? = null,
    val longitude: Any? = null,
    val authorUid: String = "",        // ✅ владелец (uid из Firebase Auth)
    val isUploaded: Boolean = false,   // ✅ опубликован ли в Firebase
    val categoryName: String? = null,  // ✅ для обратной совместимости с Firebase
    
    // ✅ Поля для баннера (если isBanner = true)
    val isBanner: Boolean = false,
    val frame: Boolean = false,
    val bannerTextTopLeft: String = "",
    val bannerTextTopRight: String = "",
    val bannerTextBottomLeft: String = "",
    val bannerPublishDate: Long = 0,
    val bannerExpireDate: Long = 0
)



fun BookDTO.toBannerEntity(): BannerEntity {
    return BannerEntity(
        key = this.key,
        postKey = this.key, // Связываем баннер с ключом поста
        imageUrl = this.imageUrl,
        authorRole = this.authorRole,
        publishPeriod = this.publishPeriod,
        timeStamp = this.timeStamp,
        authorUid = this.authorUid,
        isUploaded = this.isUploaded,
        isBanner = true, // Принудительно ставим true
    )
}