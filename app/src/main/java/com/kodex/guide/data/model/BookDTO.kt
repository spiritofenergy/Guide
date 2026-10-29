package com.kodex.guide.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kodex.guide.data.source.local.BannerEntity
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
    val categoryIndex: Any = BookCategories.OTHER.id,
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
    val latitude: Any? = null,
    val longitude: Any? = null,
    val authorUid: String = "",        // ✅ владелец (uid из Firebase Auth)
    val isUploaded: Boolean = false,   // ✅ опубликован ли в Firebase
    val categoryName: String? = null,  // ✅ для обратной совместимости с Firebase
)



fun BookDTO.toBannerEntity(): BannerEntity {
    return BannerEntity(
        key = this.key ?: "",
        postKey = this.key ?: "", // Связываем баннер с ключом поста
        telephone = this.telephone ?: "",
       // categoryIndex = this.categoryIndex ?: 0,
        imageUrl = this.imageUrl ?: "",
        authorRole = this.authorRole ?: 0,
        publishPeriod = this.publishPeriod ?: 0,
        timeStamp = this.timeStamp ?: System.currentTimeMillis(),
        authorUid = this.authorUid ?: "",
        isUploaded = this.isUploaded ?: true,
        isBanner = true, // Принудительно ставим true

    )
}