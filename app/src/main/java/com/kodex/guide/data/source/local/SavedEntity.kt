package com.kodex.guide.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kodex.guide.domain.model.Book

@Entity(tableName = "saved")
data class SavedEntity(
    @PrimaryKey val key: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val price: Int,
    val telephone: String,
    val village: String,
    val delivery: Boolean,
    val payment: Boolean,
    val location: Boolean,
    val categoryIndexId: Int, // Для отображения категории
    // Добавьте сюда остальные поля из Book, если они нужны для UI
)

// Маппер: из БД в доменную модель (для ViewModel)
fun SavedEntity.toDomain(): Book {
    return Book(
        key = key,
        title = title,
        description = description,
        imageUrl = imageUrl,
        price = price,
        telephone = telephone,
        village = village,
        delivery = delivery,
        payment = payment,
        location = location,
        // categoryIndex = BookCategories.fromId(categoryIndexId) // Если у вас есть такой метод
    )
}

// Маппер: из доменной модели в БД (для сохранения)
fun Book.toSavedEntity(): SavedEntity {
    return SavedEntity(
        key = key,
        title = title,
        description = description,
        imageUrl = imageUrl,
        price = price,
        telephone = telephone,
        village = village,
        delivery = delivery,
        payment = payment,
        location = location,
        categoryIndexId = categoryIndex.id // или как у вас достается ID категории
    )
}