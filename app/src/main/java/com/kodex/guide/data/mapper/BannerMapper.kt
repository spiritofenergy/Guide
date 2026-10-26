package com.kodex.guide.data.mapper

import com.kodex.guide.domain.model.Banner
import com.kodex.guide.ui.db.BannerDTO

/**
 * Конвертация BannerDTO → Banner
 */
fun BannerDTO.toBanner(): Banner {
    return Banner(
        id = id,
        postKey = postKey,
        imageUrl = imageUrl,
        authorUid = authorUid,
        authorRole = authorRole,
        isActive = isActive,
        createdAt = createdAt,
        publishDate = publishDate,
        expireDate = expireDate,
        categoryIndex = categoryIndex,
        phone = phone,
        textTopLeft = textTopLeft,
        textTopRight = textTopRight,
        textBottomLeft = textBottomLeft,
        textBottomRight = textBottomRight
    )
}

/**
 * Конвертация Banner → BannerDTO
 */
fun Banner.toBannerDTO(): BannerDTO {
    return BannerDTO(
        id = id,
        postKey = postKey,
        imageUrl = imageUrl,
        authorUid = authorUid,
        authorRole = authorRole,
        isActive = isActive,
        createdAt = createdAt,
        publishDate = publishDate,
        expireDate = expireDate,
        categoryIndex = categoryIndex,
        phone = phone,
        textTopLeft = textTopLeft,
        textTopRight = textTopRight,
        textBottomLeft = textBottomLeft,
        textBottomRight = textBottomRight
    )
}
