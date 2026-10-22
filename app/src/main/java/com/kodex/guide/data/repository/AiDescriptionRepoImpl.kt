package com.kodex.guide.data.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.TextPart
import com.kodex.guide.domain.repository.AiDescriptionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiDescriptionRepoImpl @Inject constructor(
    private val app: Application
) : AiDescriptionRepo {

    // Инициализируем модель.
    // FirebaseApp.getInstance() берет дефолтный инстанс, который инициализируется автоматически.
    private val generativeModel by lazy {
        FirebaseAI.getInstance(FirebaseApp.getInstance())
            .generativeModel(modelName = "gemini-3.8-flash")
    }

    override fun generateDescription(
        title: String,
        imageUri: Uri?,
        category: String
    ): Flow<String> = flow {

        val prompt = """
            Напиши короткое описание объявления на русском языке для товара со словами продам "$title" из категории "$category".
            Сделай подробное описания в 2–3 абзаца по 3-4 коротких предложения, только важная информация.
            Если есть несколько смысловых частей, раздели их заголовками. Заголовок с красной строки
            Не добавляй вступление и заключение, не выдумывай характеристики, которых нет в названии или на фото.
            В конце добавь варианты применения
        """.trimIndent()

        // В зависимости от наличия фото, выбираем способ генерации
        val response = if (imageUri != null) {
            // Конвертируем Uri в Bitmap
            val bitmap = uriToBitmap(imageUri)

            if (bitmap != null) {
                val promptContent = Content(
                    parts = listOf(TextPart(prompt), ImagePart(bitmap))
                )

                // Передаем список контента
                generativeModel.generateContent(listOf(promptContent))
            } else {
                // Если картинку не удалось загрузить, генерируем только по тексту
                generativeModel.generateContent(prompt)
            }
        } else {
            // Без фото — только текст
            generativeModel.generateContent(prompt)
        }

        // Отправляем полученный текст в Flow
        response.text?.let { text ->
            emit(text)
        }

    }.flowOn(Dispatchers.IO) // Выполняем сетевой запрос в фоновом потоке

    /**
     * Вспомогательная функция для конвертации Uri в Bitmap
     */
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return app.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
}