package com.kodex.guide.domain.model

/**
 * Простая ссылка на пост для баннера
 */
data class Banner(
    val id: String = "",
    val postKey: String = "", // Ключ поста для перехода
    val imageUrl: String = "", // Фото поста для отображения
    val authorUid: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
