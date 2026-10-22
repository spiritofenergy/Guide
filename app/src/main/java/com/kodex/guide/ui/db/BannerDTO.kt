package com.kodex.guide.ui.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DTO для хранения простых ссылок на посты в Room
 */
@Entity(tableName = "banners")
data class BannerDTO(
    @PrimaryKey
    val id: String = "",
    val postKey: String = "",
    val imageUrl: String = "",
    val authorUid: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
