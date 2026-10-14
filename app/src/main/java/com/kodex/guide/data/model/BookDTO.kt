package com.kodex.guide.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kodex.guide.domain.model.BookCategories

@Entity(tableName = "books")
data class BookDTO(
    @PrimaryKey(autoGenerate = true)
    //val id: Int = 0,
    val key: String = "",
    val title: String = "",
    val searchTitle: String = title.lowercase(),
    val description: String = "",
    val price: Int = 0,
    val telephone: String = "",
    val categoryIndex: Int = BookCategories.OTHER.id,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val isAuthor: Boolean = false,
    val authorId: Int = 0,
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
    val authorUid: String = "",        // ✅ владелец (uid из Firebase Auth)
    val isUploaded: Boolean = false,   // ✅ опубликован ли в Firebase

)