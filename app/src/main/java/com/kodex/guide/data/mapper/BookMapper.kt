package com.kodex.guide.data.mapper

import com.kodex.guide.data.model.BookDTO
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import kotlin.String

fun BookDTO.toBook(): Book {
    return Book(
       // id = id,
        key = key,
        title = title,
        searchTitle = title.lowercase(),
        description = description,
        price = price,
        telephone = telephone,
        categoryIndex = BookCategories.fromId(categoryIndex),
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isAuthor = isAuthor,
        authorId = authorId,
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

fun Book.toBookDTO(): BookDTO {
    return BookDTO(
       // id = id,
        key = key,
        title = title,
        searchTitle = title.lowercase(),
        description = description,
        price = price,
        telephone = telephone,
        categoryIndex = categoryIndex.id,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isAuthor = isAuthor,
        authorId = authorId,
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