package com.kodex.guide.domain.model

data class BookFirestore(
    val id: Int = 0,
    val key: String = "",
    val title: String = "",
    val searchTitle: String = "",
    val description: String = "",
    val price: Int = 0,
    val telephone: String = "",
    val categoryIndex: Int = BookCategories.OTHER.id, // Сохраняем как Int
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val isAuthor: Boolean = false,
    val authorId: Int = 0,
    val publishPeriod: Int = 1,
    val timeStamp: Long = System.currentTimeMillis(),
    val deleteDate: Int = 0,
    val village: String = "",
    val delivery: Boolean = false,
    val ratingsList: List<Int> = emptyList(),
) {
    fun toBook(): Book = Book(
        id = id,
        key = key,
        title = title,
        searchTitle = searchTitle,
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
        delivery = delivery,
        ratingsList = ratingsList
    )
}

