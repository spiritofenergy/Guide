package com.kodex.guide.data.mapper

import com.kodex.guide.domain.model.Book
import com.kodex.guide.presentation.navigation.NavRoutes
import java.util.concurrent.ConcurrentHashMap

object ParallaxImageCache {
    private val imagesByBookId = ConcurrentHashMap<String, String>()

    fun take(bookId: String): String? = imagesByBookId.remove(bookId)

    fun put(bookId: String, imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            imagesByBookId[bookId] = imageUrl
        } else {
            imagesByBookId.remove(bookId)
        }
    }
}

fun Book.toParallaxNavObject(): NavRoutes.ParallaxNavObject {
    ParallaxImageCache.put(key, imageUrl)
    return NavRoutes.ParallaxNavObject(
        bookId = key,
        title = title,
        description = description,
        price = price,
        village = village,
        street = street,
        house = house,
        flat = flat,
        location = location,
        categoryIndex = categoryIndex,
        telephone = telephone,
        payment = payment,
        delivery = delivery,
        ratingsList = ratingsList,
        latitude = latitude.orEmpty(),
        longitude = longitude.orEmpty(),
        timestamp = timeStamp,
        publishPeriod = publishPeriod

        // все остальные поля возьмутся из дефолтных значений
    )
}