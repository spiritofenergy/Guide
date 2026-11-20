package com.kodex.guide.data.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class AndroidImagePreparer @Inject constructor(
    @ApplicationContext private val context: Context
) : ImagePreparer {

    override suspend fun prepare(uri: Uri?, existingImage: String?): Bitmap? =
        withContext(Dispatchers.IO) {
            val raw = when {
                uri != null -> context.contentResolver
                    .openInputStream(uri)
                    ?.use(BitmapFactory::decodeStream)

                existingImage.isNullOrBlank() -> null
                existingImage.startsWith("http") -> loadFromUrl(existingImage)
                else -> decodeBase64(existingImage)
            }
            raw?.let { downscale(it, MAX_DIMEN) }
        }

    private fun loadFromUrl(url: String): Bitmap? = runCatching {
        URL(url).openStream().use(BitmapFactory::decodeStream)
    }.getOrNull()

    private fun decodeBase64(value: String): Bitmap? = runCatching {
        // Убираем data:image/...;base64, если пришло как data-url
        val clean = value.substringAfter("base64,", value)
        val bytes = Base64.decode(clean, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()

    private fun downscale(src: Bitmap, maxDim: Int): Bitmap {
        val maxSide = max(src.width, src.height)
        if (maxSide <= maxDim) return src
        val ratio = maxDim.toFloat() / maxSide
        return Bitmap.createScaledBitmap(
            src,
            (src.width * ratio).toInt(),
            (src.height * ratio).toInt(),
            true
        )
    }

    private companion object {
        const val MAX_DIMEN = 1024
    }
}