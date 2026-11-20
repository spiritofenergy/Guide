package com.kodex.guide.domain.ai


    import android.graphics.Bitmap

    interface PostDescriptionGenerator {
        suspend fun generate(title: String, image: Bitmap?): Result<String>
    }
