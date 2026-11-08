package com.kodex.guide.presentation.myPosts.components

import android.graphics.BitmapFactory
import android.util.Base64

fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}