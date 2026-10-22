package com.kodex.guide.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

/**
 * Repository для генерации описаний объявлений через Firebase AI (Gemini).
 */
interface AiDescriptionRepo {
    /**
     * Генерирует описание по названию и опциональному фото.
     * @param title Заголовок объявления
     * @param imageUri Опциональное фото (null если нет)
     * @param category Категория для контекста
     * @return Flow с генерируемым текстом (отправляется чанками)
     */
    fun generateDescription(
        title: String,
        imageUri: Uri? = null,
        category: String = ""
    ): Flow<String>
}
