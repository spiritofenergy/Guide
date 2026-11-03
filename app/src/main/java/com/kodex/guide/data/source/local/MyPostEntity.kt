package com.kodex.guide.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mypost")
data class MyPostEntity(
    @PrimaryKey
    val key: String,
    val title: String,
    val description: String,
    val price: Int,
    val telephone: String,
    val village: String,
    val street: String,
    val house: String,
    val flat: String,
    val categoryIndex: Int,
    val delivery: Boolean,
    val payment: Boolean,
    val location: Boolean,
    val publishPeriod: Int,
    val imageUrl: String,          // base64 или http
    val isUploaded: Boolean,
    val latitude: String?,
    val longitude: String?,
    // Баннер
    val isBanner: Boolean,
    val bannerTextTopLeft: String,
    val bannerTextTopRight: String,
    val bannerTextBottomLeft: String,
    val bannerTextBottomRight: String,
    val bannerPublishDate: Long,
    val bannerExpireDate: Long,
    // для сортировки «свежие сверху»
    val updatedAt: Long = System.currentTimeMillis()
)