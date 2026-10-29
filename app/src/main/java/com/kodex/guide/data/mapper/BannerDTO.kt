package com.kodex.guide.data.mapper

import com.kodex.guide.data.model.BannerDTO
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.domain.model.BookCategories

// Конвертация из Firebase в Room
fun BannerDTO.toEntity(): BannerEntity {
    return BannerEntity(
        id = this.id,
        key = this.key,
        postKey = this.postKey,
        // Если в DTO categoryIndex это Int, а в Entity это BookCategories, нужно найти по id
        categoryIndex = BookCategories.entries.find { it.id == this.categoryIndex } ?: BookCategories.OTHER,
        imageUrl = this.imageUrl,
        authorRole = this.authorRole,
        publishPeriod = this.publishPeriod,
        timeStamp = this.timeStamp,
        deleteDate = this.deleteDate,
        authorUid = this.authorUid,
        isUploaded = this.isUploaded,
        isBanner = this.isBanner,
        bannerTextTopLeft = this.bannerTextTopLeft,
        bannerTextTopRight = this.bannerTextTopRight,
        bannerTextBottomLeft = this.bannerTextBottomLeft,
        bannerTextBottomRight = this.bannerTextBottomRight,
        bannerPublishDate = this.bannerPublishDate,
        bannerExpireDate = this.bannerExpireDate
    )
}