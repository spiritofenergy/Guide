package com.kodex.guide.data.mapper

import com.kodex.guide.data.model.BannerDTO
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.domain.model.Banner
import com.kodex.guide.domain.model.BookCategories

// Domain -> Entity (Для сохранения в Room)
fun Banner.toEntity(): BannerEntity {
    return BannerEntity(
        id = this.id,
        key = this.key,
        telephone = this.telephone,
        postKey = this.postKey,
        categoryIndex = this.categoryIndex,
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

// Entity -> Domain (Для отправки в UI)
fun BannerEntity.toDomain(): Banner {
    return Banner(
        id = this.id,
        key = this.key,
        telephone = this.telephone,
        postKey = this.postKey,
        categoryIndex = this.categoryIndex,
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

// Domain -> DTO (Для отправки в Firebase)
fun Banner.toDTO(): BannerDTO {
    return BannerDTO(
        id = this.id,
        key = this.key,
        telephone = this.telephone,
        postKey = this.postKey,
        categoryIndex = this.categoryIndex.id, // Если BookCategories имеет поле id
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

// DTO -> Domain (При получении из Firebase)
fun BannerDTO.toDomain(): Banner {
    return Banner(
        id = this.id,
        key = this.key,
        telephone = this.telephone,
        postKey = this.postKey,
        // Здесь может потребоваться логика поиска enum по id, если это не прямая конвертация
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