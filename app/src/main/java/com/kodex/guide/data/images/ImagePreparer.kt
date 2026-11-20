package com.kodex.guide.data.images

import android.graphics.Bitmap
import android.net.Uri

interface ImagePreparer {
    /**
     * Вернёт Bitmap из Uri (новое фото) или base64/http-url (существующее фото).
     * Может вернуть null — тогда генерация пойдёт только по названию.
     */
    suspend fun prepare(uri: Uri?, existingImage: String?): Bitmap?
}