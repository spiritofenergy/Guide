package com.kodex.guide.data.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Тонкая адаптация существующего пайплайна для AI:
 * ImageLoader → BitmapResizer.
 * Сжатие/кодирование НЕ делаем — Gemini принимает Bitmap напрямую.
 */
@Singleton
class ImagePreparerImpl @Inject constructor(
    private val imageLoader: ImageLoader,
    private val bitmapResizer: BitmapResizer
) : ImagePreparer {

    override suspend fun prepare(uri: Uri?, existingImage: String?): Bitmap? =
        withContext(Dispatchers.IO) {
            val raw = when {
                uri != null -> runCatching { imageLoader.loadImage(uri) }.getOrNull()
                existingImage.isNullOrBlank() -> null
                existingImage.startsWith("http") -> loadFromUrl(existingImage)
                else -> existingImage.toBitmap()
            }
            raw?.let { bitmapResizer.resize(it, AI_MAX_DIMEN) }
        }

    private fun loadFromUrl(url: String): Bitmap? = runCatching {
        URL(url).openStream().use(BitmapFactory::decodeStream)
    }.getOrNull()

    private companion object {
        const val AI_MAX_DIMEN = 1024
    }
}