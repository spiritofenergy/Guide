package com.kodex.guide.domain.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.kodex.guide.domain.ai.PostDescriptionGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePostDescriptionGenerator @Inject constructor() : PostDescriptionGenerator {

    private val TAG = "FirebaseDescriptionGen"

    // Ленивая инициализация: модель создаётся один раз.
    private val model by lazy {
        try {
            Log.d(TAG, "Initializing Firebase AI model...")
            FirebaseAI.getInstance(FirebaseApp.getInstance())
                .generativeModel(MODEL_NAME).also {
                    Log.d(TAG, "Firebase AI model initialized successfully")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase AI model", e)
            throw e
        }
    }

    override suspend fun generate(title: String, image: Bitmap?): Result<String> = runCatching {
        val prompt = buildPrompt(title, hasImage = image != null)
        val response = model.generateContent(prompt)
        response.text?.trim().orEmpty().ifEmpty {
            error("Модель вернула пустой ответ")
        }
    }

    private fun buildPrompt(title: String, hasImage: Boolean): String = buildString {
        append("Название объявления: \"").append(title).append("\"\n")
        if (hasImage) append("На фото — предмет или услуга объявления.\n")
        append("Напиши привлекательное описание на русском: 2–4 предложения, ")
        append("без markdown, без эмодзи, без кавычек вокруг ответа.")
    }

    private companion object {
        const val MODEL_NAME = "gemini-2.0-flash"
    }
}
