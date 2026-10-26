package com.kodex.guide.domain.model

/**
 * Баннер с детальной информацией
 */
data class Banner(
    val id: String = "",
    val postKey: String = "", // Ключ поста для перехода
    val imageUrl: String = "", // Фото поста для отображения
    val authorUid: String = "",
    val authorRole: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    
    // Новые поля
    val publishDate: Long = System.currentTimeMillis(), // Дата публикации (timestamp)
    val expireDate: Long = 0, // Дата удаления/истечения (timestamp, 0 = нет ограничения)
    val categoryIndex: Int = 0, // Категория баннера (индекс)
    val phone: String = "", // Телефон
    val textTopLeft: String = "", // Текст слева наверху
    val textTopRight: String = "", // Текст справа наверху
    val textBottomLeft: String = "", // Текст слева внизу
    val textBottomRight: String = "" // Текст справа внизу
)
