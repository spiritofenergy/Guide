package com.kodex.guide.data.mapper

import com.kodex.guide.data.source.local.MyPostEntity
import com.kodex.guide.domain.model.Book
import com.kodex.guide.data.model.BannerDTO

object MyPostMapper {
        fun entityToDomain(e: MyPostEntity): Book = Book(
            id = 0, // Локальный ID Room не использует, ставим 0
            key = e.key,
            title = e.title,
            description = e.description,
            price = e.price,
            telephone = e.telephone,
            openingHours = "", // Если в Book это поле есть, но в Entity нет — ставим пустую строку
            village = e.village,
            street = e.street,
            house = e.house,
            flat = e.flat,
            categoryIndex = e.categoryIndex,
            delivery = e.delivery,
            payment = e.payment,
            location = e.location,
            publishPeriod = e.publishPeriod,
            imageUrl = e.imageUrl,
            isUploaded = e.isUploaded,
            latitude = e.latitude,
            longitude = e.longitude,
            isBanner = e.isBanner,
            bannerTextTopLeft = e.bannerTextTopLeft,
            bannerTextTopRight = e.bannerTextTopRight,
            bannerTextBottomLeft = e.bannerTextBottomLeft,
         //   bannerTextBottomRight = e.bannerTextBottomRight,
            bannerPublishDate = e.bannerPublishDate,
            bannerExpireDate = e.bannerExpireDate,
            // Поля ниже отсутствуют в MyPostEntity, поэтому ставим дефолты


            isAuthor = true,
            authorRole = 0,
            searchTitle = e.title.lowercase(),
            timeStamp = e.updatedAt // Используем updatedAt как timeStamp
        )

        fun domainToEntity(b: Book): MyPostEntity = MyPostEntity(
            key = b.key,
            title = b.title,
            description = b.description,
            price = b.price,
            telephone = b.telephone,
            village = b.village,
            street = b.street,
            house = b.house,
            flat = b.flat,
            categoryIndex = b.categoryIndex,
            delivery = b.delivery,
            payment = b.payment,
            location = b.location,
            publishPeriod = b.publishPeriod,
            imageUrl = b.imageUrl,
            isUploaded = b.isUploaded,
            latitude = b.latitude,
            longitude = b.longitude,
            isBanner = b.isBanner,
            bannerTextTopLeft = b.bannerTextTopLeft,
            bannerTextTopRight = b.bannerTextTopRight,
            bannerTextBottomLeft = b.bannerTextBottomLeft,
          //  bannerTextBottomRight = b.bannerTextBottomRight,
            bannerPublishDate = b.bannerPublishDate,
            bannerExpireDate = b.bannerExpireDate,
            // ⚠️ ОБЯЗАТЕЛЬНО обновляем дату при каждом сохранении, иначе сортировка сломается
            updatedAt = System.currentTimeMillis()
        )
    }


fun Book.toBannerDTO(): BannerDTO {
    return BannerDTO(
        key = key,
        imageUrl = imageUrl ?: "",
        authorRole = authorRole,
        publishPeriod = publishPeriod,
        timeStamp = timeStamp,
        authorUid = authorUid,
        isUploaded = isUploaded,

        // ✅ ВОТ ОНИ - НАШИ ПОЛЯ!
        isBanner = true,
        bannerTextTopLeft = bannerTextTopLeft,
        bannerTextTopRight = bannerTextTopRight,
        bannerTextBottomLeft = bannerTextBottomLeft,
        bannerPublishDate = bannerPublishDate,
        bannerExpireDate = bannerExpireDate
    )
}