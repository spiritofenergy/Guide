package com.kodex.guide.domain.model
import android.net.Uri

/**
 * Единое состояние формы редактора объявлений.
 * Заменяет собой 15+ отдельных mutableStateOf в MyPostEditorScreen.
 */
data class PostEditorState(
    val title: String = "",
    val description: String = "",
    val price: String = "",          // Храним как String для безопасной работы с TextField
    val telephone: String = "",
    val village: String = "",
    val street: String = "",
    val house: String = "",
    val flat: String = "",
    val category: BookCategories = BookCategories.SERVICES,
    val delivery: Boolean = false,
    val payment: Boolean = false,
    val location: Boolean = false,
    val imageUri: Uri? = null,       // Выбранное фото (из галереи или камеры)
    val existingImageUrl: String = "" // URL/Base64 существующего фото при редактировании
) {

    fun toBook(): Book {
        return Book(
            title = title,
            description = description,
            price = price.toIntOrNull() ?: 0,
            telephone = telephone,
            village = village,
            street = street,
            house = house,
            flat = flat,
            categoryIndex = category,
            delivery = delivery,
            payment = payment,
            location = location,
            imageUrl = if (imageUri != null) "" else existingImageUrl

        )
    }

    companion object {
        /**
         * Создает состояние формы из существующего Book (для режима редактирования).
         */
        fun fromBook(book: Book): PostEditorState = PostEditorState(
            title = book.title,
            description = book.description,
            price = book.price.toString(),
            telephone = book.telephone,
            village = book.village,
            street = book.street,
            house = book.house,
            flat = book.flat,
            category = book.categoryIndex,
            delivery = book.delivery,
            payment = book.payment,
            location = book.location,
            existingImageUrl = book.imageUrl ?: ""
        )
    }
}