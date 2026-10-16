package com.kodex.guide.data.mapper

import android.util.Log
import com.kodex.guide.data.model.BookDTO
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import kotlin.String

fun BookDTO.toBook(): Book {
    // ✅ Конвертируем categoryIndex из Any (Double/Int/String) в Int
    val categoryId = when (categoryIndex) {
        is Int -> categoryIndex
        is Long -> categoryIndex.toInt()
        is Double -> categoryIndex.toInt()
        is Float -> categoryIndex.toInt()
        is String -> categoryIndex.toIntOrNull()
            ?: BookCategories.entries.indexOfFirst { it.name.equals(categoryIndex, ignoreCase = true) }
        else -> {
            Log.e("BookMapper", "Unknown categoryIndex type: ${categoryIndex::class.java.name}, value: $categoryIndex")
            BookCategories.OTHER.id
        }
    }
    
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
        isFavorite = isFavorite,
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
        categoryIndex = categoryIndex.id,  // ✅ Сохраняем как Int
        categoryName = categoryIndex.name,  // ✅ Имя для обратной совместимости
        imageUrl = imageUrl,
        isFavorite = isFavorite,
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