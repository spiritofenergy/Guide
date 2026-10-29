package com.kodex.guide.data.model

import com.kodex.guide.domain.model.BookCategories

data class BannerDTO(
    val id: Int = 0,
    val key: String = "",
    val telephone: String = "",
    val postKey: String = "",
    val categoryIndex: Int = BookCategories.OTHER.id, // Firebase лучше хранить Int
    val imageUrl: String = "",
    val authorRole: Int = 0,
    val publishPeriod: Int = 1,
    val timeStamp: Long = System.currentTimeMillis(),
    val deleteDate: Int = 0,
    val authorUid: String = "",
    val isUploaded: Boolean = false,
    val isBanner: Boolean = true,
    val bannerTextTopLeft: String = "",
    val bannerTextTopRight: String = "",
    val bannerTextBottomLeft: String = "",
    val bannerTextBottomRight: String = "",
    val bannerPublishDate: Long = 0,
    val bannerExpireDate: Long = 0
)