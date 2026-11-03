package com.kodex.guide.data.mapper

import com.kodex.guide.data.source.local.MyPostEntity
import com.kodex.guide.domain.model.Book

object MyPostMapper {

    fun entityToDomain(e: MyPostEntity): Book = Book(
        key = e.key,
        title = e.title,
        description = e.description,
        price = e.price,
        telephone = e.telephone,
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
        bannerTextBottomRight = e.bannerTextBottomRight,
        bannerPublishDate = e.bannerPublishDate,
        bannerExpireDate = e.bannerExpireDate,
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
        bannerTextBottomRight = b.bannerTextBottomRight,
        bannerPublishDate = b.bannerPublishDate,
        bannerExpireDate = b.bannerExpireDate,
    )
}