package com.kodex.guide.data.mapper

import com.kodex.guide.data.source.local.DraftEntity
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.domain.model.BookCategories

object DraftMapper {
    fun toEntity(state: PostEditorState): DraftEntity = DraftEntity(
        title = state.title.takeIf { it.isNotBlank() },
        description = state.description.takeIf { it.isNotBlank() },
        price = state.price.toIntOrNull(),
        telephone = state.telephone.takeIf { it.isNotBlank() },
        village = state.village.takeIf { it.isNotBlank() },
        street = state.street.takeIf { it.isNotBlank() },
        house = state.house.takeIf { it.isNotBlank() },
        flat = state.flat.takeIf { it.isNotBlank() },
        categoryId = state.category.id,
        delivery = state.delivery,
        payment = state.payment,
        location = state.location,
        imageUrl = state.existingImageUrl.takeIf { it.isNotBlank() },
        
        // Баннер-поля
        isBanner = state.isBanner,
        bannerTextTopLeft = state.bannerTextTopLeft.takeIf { it.isNotBlank() },
        bannerTextTopRight = state.bannerTextTopRight.takeIf { it.isNotBlank() },
        bannerTextBottomLeft = state.bannerTextBottomLeft.takeIf { it.isNotBlank() },
        bannerTextBottomRight = state.bannerTextBottomRight.takeIf { it.isNotBlank() },
        bannerPhone = state.bannerPhone.takeIf { it.isNotBlank() },
        bannerPublishDate = state.bannerPublishDate,
        bannerExpireDate = state.bannerExpireDate,
        bannerCategoryId = if (state.bannerCategoryIndex > 0) state.bannerCategoryIndex else null
    )

    fun toState(entity: DraftEntity): PostEditorState = PostEditorState(
        title = entity.title ?: "",
        description = entity.description ?: "",
        price = entity.price?.toString() ?: "",
        telephone = entity.telephone ?: "",
        village = entity.village ?: "",
        street = entity.street ?: "",
        house = entity.house ?: "",
        flat = entity.flat ?: "",
        category = BookCategories.entries.find { it.id == entity.categoryId } ?: BookCategories.SERVICES,
        delivery = entity.delivery,
        payment = entity.payment,
        location = entity.location,
        existingImageUrl = entity.imageUrl ?: "",
        
        // Баннер-поля
        isBanner = entity.isBanner,
        bannerTextTopLeft = entity.bannerTextTopLeft ?: "",
        bannerTextTopRight = entity.bannerTextTopRight ?: "",
        bannerTextBottomLeft = entity.bannerTextBottomLeft ?: "",
        bannerTextBottomRight = entity.bannerTextBottomRight ?: "",
        bannerPhone = entity.bannerPhone ?: "",
        bannerPublishDate = entity.bannerPublishDate,
        bannerExpireDate = entity.bannerExpireDate,
        bannerCategoryIndex = entity.bannerCategoryId ?: 0
    )
}