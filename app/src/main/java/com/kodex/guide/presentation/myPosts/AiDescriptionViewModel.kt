package com.kodex.guide.presentation.myPosts

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.domain.usecase.GenerateAiDescriptionUseCase
import com.google.firebase.ai.type.QuotaExceededException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * ViewModel для AI-генерации описаний.
 */
@HiltViewModel
class AiDescriptionViewModel @Inject constructor(
    private val generateAiDescription: GenerateAiDescriptionUseCase
) : ViewModel() {
    
    private val _descriptionFlow = MutableStateFlow<String?>(null)
    val descriptionFlow: StateFlow<String?> = _descriptionFlow.asStateFlow()
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun generate(
        title: String,
        imageUri: Uri? = null,
        category: String = ""
    ) {
        if (_isGenerating.value) return

        viewModelScope.launch {
            _isGenerating.value = true
            _descriptionFlow.value = null
            _errorMessage.value = null

            try {
                generateAiDescription(title, imageUri, category)
                    .collect { description ->
                        _descriptionFlow.value = description
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("AiDescription", "Description generation failed", exception)
                val causes = exception.causes().toList()
                _errorMessage.value = when {
                    causes.any { it is QuotaExceededException } ->
                        "Закончились токены. Попробуйте через 15 часов."
                    causes.any { it is UnknownHostException } ->
                        "Нет соединения с сервером. Проверьте интернет и повторите попытку."
                    else ->
                        "Не удалось сгенерировать описание. Попробуйте ещё раз."
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }
    
    fun clear() {
        _descriptionFlow.value = null
        _errorMessage.value = null
        _isGenerating.value = false
    }

    private fun Throwable.causes(): Sequence<Throwable> = sequence {
        var cause: Throwable? = this@causes
        while (cause != null) {
            yield(cause)
            cause = cause.cause
        }
    }
}
