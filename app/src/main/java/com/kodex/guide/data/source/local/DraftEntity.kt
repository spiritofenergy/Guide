package com.kodex.guide.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Данные формы
    val title: String?,
    val description: String?,
    val price: Int?,
    val telephone: String?,
    val village: String?,
    val street: String?,
    val house: String?,
    val flat: String?,
    val categoryId: Int?,
    val delivery: Boolean,
    val payment: Boolean,
    val location: Boolean,
    val imageUrl: String?, // Base64 или URL
    
    // Баннер-поля
    val isBanner: Boolean = false,
    val bannerTextTopLeft: String? = null,
    val bannerTextTopRight: String? = null,
    val bannerTextBottomLeft: String? = null,
    val bannerTextBottomRight: String? = null,
    val bannerPhone: String? = null,
    val bannerPublishDate: Long = 0L,
    val bannerExpireDate: Long = 0L,
    val bannerCategoryId: Int? = null,

    // Метаданные
    val createdAt: Long = System.currentTimeMillis()
)