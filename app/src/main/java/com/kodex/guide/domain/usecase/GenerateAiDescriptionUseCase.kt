package com.kodex.guide.domain.usecase

import android.net.Uri
import com.kodex.guide.domain.repository.AiDescriptionRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для генерации описания объявления через AI.
 */
class GenerateAiDescriptionUseCase @Inject constructor(
    private val aiDescriptionRepo: AiDescriptionRepo
) {
    operator fun invoke(
        title: String,
        imageUri: Uri? = null,
        category: String = ""
    ): Flow<String> {
        return aiDescriptionRepo.generateDescription(title, imageUri, category)
    }
}
