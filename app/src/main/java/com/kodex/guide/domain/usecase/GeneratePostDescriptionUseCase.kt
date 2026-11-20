package com.kodex.guide.domain.usecase


import android.net.Uri
import com.kodex.guide.data.images.ImagePreparer
import com.kodex.guide.domain.ai.PostDescriptionGenerator
import javax.inject.Inject

class GeneratePostDescriptionUseCase @Inject constructor(
    private val generator: PostDescriptionGenerator,
    private val imagePreparer: ImagePreparer
) {
    suspend operator fun invoke(
        title: String,
        imageUri: Uri?,
        existingImage: String?
    ): Result<String> {
        val cleanTitle = title.trim()
        if (cleanTitle.length < 2) {
            return Result.failure(IllegalArgumentException("Сначала введите название объявления"))
        }
        val image = imagePreparer.prepare(imageUri, existingImage)
        return generator.generate(cleanTitle, image)
    }
}