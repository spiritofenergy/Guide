package com.kodex.guide.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.ui.db.BookConverter


@Entity(tableName = "banners")
@TypeConverters(BookConverter::class) // Можно указать здесь, если не указали в Database
data class BannerEntity(
    // @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @PrimaryKey
    val key: String = "",
    val telephone: String = "",
    val postKey: String = "",
    val categoryIndex: BookCategories = BookCategories.OTHER,
    val imageUrl: String = "",
    val authorRole: Int = 0,  // ✅ Роль автора (UserRole.level)
    val publishPeriod: Int = 1,
    val timeStamp: Long = System.currentTimeMillis(),
    val deleteDate: Int = 0,
    val authorUid: String = "",        // ✅ владелец (uid из Firebase Auth)
    val isUploaded: Boolean = false,   // ✅ опубликован ли в Firebase

    // ✅ Поля для баннера (если isBanner = true)
    val isBanner: Boolean = false,      // 🆕 Флаг что это баннер
    val bannerTextTopLeft: String = "", // 🆕 Текст слева сверху
    val bannerTextTopRight: String = "", // 🆕 Текст справа сверху
    val bannerTextBottomLeft: String = "", // 🆕 Текст слева снизу
    val bannerTextBottomRight: String = "", // 🆕 Текст справа снизу
    val bannerPublishDate: Long = 0,    // 🆕 Дата публикации баннера
    val bannerExpireDate: Long = 0,     // 🆕 Дата окончания показа баннера

)
