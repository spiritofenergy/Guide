package com.kodex.guide.ui.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DTO для хранения баннеров в Room
 */
@Entity(tableName = "banners")
data class BannerDTO(
    @PrimaryKey
    val id: String = "",
    val postKey: String = "",
    val imageUrl: String = "",
    val authorUid: String = "",
    val authorRole: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    
    // Новые поля
    val publishDate: Long = System.currentTimeMillis(),
    val expireDate: Long = 0,
    val categoryIndex: Int = 0,
    val phone: String = "",
    val textTopLeft: String = "",
    val textTopRight: String = "",
    val textBottomLeft: String = "",
    val textBottomRight: String = ""
)
