package com.kodex.guide.data.mapper

import android.util.Log
import com.kodex.guide.data.model.BookDTO
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import kotlin.String

fun BookDTO.toBook(): Book {
    // ✅ Конвертируем categoryIndex из Double (Firestore) в Int
    val categoryId = categoryIndex.toInt()
    
    Log.d("BookMapper", "title=${title}, categoryIndex=$categoryIndex (${categoryIndex::class.java.simpleName}) → categoryId=$categoryId → ${BookCategories.fromId(categoryId).name}")

    return Book(
       // id = id,
        key = key,
        title = title,
        searchTitle = title.lowercase(),
        description = description,
        price = price,
        telephone = telephone,
        categoryIndex = BookCategories.fromId(categoryId),
        imageUrl = imageUrl,
        isAuthor = isAuthor,
        authorRole = authorRole,
        publishPeriod = publishPeriod,
        timeStamp = timeStamp,
        deleteDate = deleteDate,
        village = village,
        street = street,
        house = house,
        flat = flat,
        location = location,
        delivery = delivery,
        payment = payment,
        ratingsList = ratingsList,
        latitude = latitude?.toString(),
        longitude = longitude?.toString(),
        authorUid = authorUid,
        isUploaded = isUploaded,
    )
}

fun Book.toBookDTO(): BookDTO {
    return BookDTO(
       // id = id,
        key = key,
        title = title,
        searchTitle = title.lowercase(),
        description = description,
        price = price,
        telephone = telephone,
        categoryIndex = categoryIndex.id.toDouble(),
        categoryName = categoryIndex.name,  // ✅ Имя для обратной совместимости
        imageUrl = imageUrl,
        isAuthor = isAuthor,
        authorRole = authorRole,  // ✅ authorRole → authorRole
        publishPeriod = publishPeriod,
        timeStamp = timeStamp,
        deleteDate = deleteDate,
        village = village,
        street = street,
        house = house,
        flat = flat,
        location = location,
        delivery = delivery,
        payment = payment,
        ratingsList = ratingsList,
        latitude = latitude,
        longitude = longitude,
        authorUid = authorUid,
        isUploaded = isUploaded,
    )
}
fun Book.toBannerEntity(): BannerEntity {
    return BannerEntity(
        key = this.key,
        postKey = this.key, // Жесткая связь с постом
        telephone = this.telephone,
        categoryIndex = this.categoryIndex,
        imageUrl = this.imageUrl,
        authorRole = this.authorRole,
        publishPeriod = this.publishPeriod,
        timeStamp = this.timeStamp,
        authorUid = this.authorUid,
        isUploaded = this.isUploaded,
        isBanner = true, // Баннер всегда true
        bannerTextTopLeft = this.bannerTextTopLeft,
        bannerTextTopRight = this.bannerTextTopRight,
        bannerTextBottomLeft = this.bannerTextBottomLeft,
        bannerTextBottomRight = this.bannerTextBottomRight,
        bannerPublishDate = System.currentTimeMillis(),
        bannerExpireDate = this.bannerExpireDate
    )
}
fun BookDTO.toDomain(): Book {
    return Book(
        key = this.key,
        title = this.title,
        description = this.description,
        price = this.price,
        telephone = this.telephone,
        village = this.village,
        street = this.street,
        house = this.house,
        flat = this.flat,
        latitude = this.latitude?.toString(),
        longitude = this.longitude?.toString(),
        categoryIndex = BookCategories.OTHER,
        location = this.location,
        delivery = this.delivery,
        payment = this.payment,
        imageUrl = this.imageUrl,
        authorUid = this.authorUid,
        authorRole = this.authorRole,
        isUploaded = this.isUploaded,
        timeStamp = this.timeStamp,
        publishPeriod = this.publishPeriod,
       /* openingHours = this.openingHours,
        isBanner = this.isBanner,
        bannerTextTopLeft = this.bannerTextTopLeft,
        bannerTextTopRight = this.bannerTextTopRight,
        bannerTextBottomLeft = this.bannerTextBottomLeft,
        bannerTextBottomRight = this.bannerTextBottomRight,
        bannerPublishDate = this.bannerPublishDate,
        bannerExpireDate = this.bannerExpireDate*/
        // Добавьте сюда остальные поля, которые есть в вашей модели Book и BookDTO
    )
}