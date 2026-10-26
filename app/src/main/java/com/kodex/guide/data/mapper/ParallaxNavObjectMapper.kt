package com.kodex.guide.data.mapper

import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.presentation.navigation.NavRoutes
import java.util.concurrent.ConcurrentHashMap

object ParallaxImageCache {
    private val imagesByBookId = ConcurrentHashMap<String, String>()
    private val navObjectsByBookId = ConcurrentHashMap<String, NavRoutes.ParallaxNavObject>()

    fun take(bookId: String): NavRoutes.ParallaxNavObject? = navObjectsByBookId.remove(bookId)

    fun takeImageUrl(bookId: String): String? = imagesByBookId.remove(bookId)

    fun put(bookId: String, imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            imagesByBookId[bookId] = imageUrl
        } else {
            imagesByBookId.remove(bookId)
        }
    }
    
    fun putNavObject(bookId: String, navObject: NavRoutes.ParallaxNavObject) {
        navObjectsByBookId[bookId] = navObject
    }
    
    fun getNavObject(bookId: String): NavRoutes.ParallaxNavObject? = navObjectsByBookId[bookId]
}

/**
 * Создаёт ParallaxNavObject со ВСЕМИ полями из Book и сохраняет в кеш.
 * Используется при клике на книгу для передачи данных через навигацию.
 * Данные кэшируются, чтобы не передавать большие строки (например Base64) через Intent.
 */
fun Book.toParallaxNavObject(): NavRoutes.ParallaxNavObject {
    // Сохраняем полный объект в кеш для получения данных после навигации
    val navObject = NavRoutes.ParallaxNavObject(
        bookId = key,
        title = title,
        description = description,
        price = price,
        telephone = telephone,
        village = village,
        street = street,
        house = house,
        flat = flat,
        location = location,
        delivery = delivery,
        payment = payment,
        latitude = latitude ?: "",
        longitude = longitude ?: "",
        categoryIndex = categoryIndex,
        imageUrl = imageUrl,
        author = "",
        authorEmail = authorEmail,
        timestamp = timeStamp,
        isFaves = isFavorite,
        ratingsList = ratingsList,
        hasDelivery = delivery,
        acceptsCard = payment,
        publishPeriod = publishPeriod
    )
    
    // Сохраняем в кеш для получения данных ПОСЛЕ навигации (не передаём через Intent!)
    ParallaxImageCache.putNavObject(key, navObject)
    ParallaxImageCache.put(key, imageUrl)
    
    // Возвращаем ТОЛЬКО bookId для навигации (чтобы не превышать лимит длины URL)
    return NavRoutes.ParallaxNavObject(bookId = key)
}