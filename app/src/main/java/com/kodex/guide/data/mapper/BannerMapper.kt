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
        isActive = isActive,
        createdAt = createdAt
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
        isActive = isActive,
        createdAt = createdAt
    )
}
