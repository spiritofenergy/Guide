package com.kodex.guide.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kodex.guide.ui.db.BookConverter

@Entity(tableName ="books")
@TypeConverters(BookConverter::class) // Можно указать здесь, если не указали в Database
data class Book(
   // @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @PrimaryKey
    val key: String ="",
    val title: String ="",
    val description: String ="",
    val price: Int = 0,
    val telephone: String = "",
    val categoryIndex: BookCategories = BookCategories.OTHER,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val isAuthor: Boolean = false,
    val authorRole: Int = 0,  // ✅ Роль автора (UserRole.level)
    val publishPeriod: Int = 1,
    val timeStamp: Long = System.currentTimeMillis(),
    val deleteDate: Int = 0,
    val village: String = "",
    val street: String = "",
    val house: String = "",
    val flat: String = "",
    val location: Boolean = false,
    val delivery: Boolean = false,
    val payment: Boolean = false,
    val ratingsList: List<Int> = emptyList(),
    val latitude: String? = null,
    val longitude: String? = null,
    val searchTitle: String = title.lowercase(),
    val authorUid: String = "",        // ✅ владелец (uid из Firebase Auth)
    val isUploaded: Boolean = false,   // ✅ опубликован ли в Firebase

    )