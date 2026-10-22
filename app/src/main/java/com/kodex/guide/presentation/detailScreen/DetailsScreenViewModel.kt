package com.kodex.guide.presentation.detailScreen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.domain.repository.BooksRepo
import com.kodex.guide.domain.usecase.GetRelatedBooksUseCase
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.presentation.detailScreen.states.DetailsUiState
import com.kodex.guide.presentation.events.DetailUiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    val booksRepo: BooksRepo,
    private val getRelatedBooksUseCase: GetRelatedBooksUseCase

) : ViewModel() {
    // ✅ НОВОЕ: похожие посты
    private val _relatedBooks = MutableStateFlow<List<Book>>(emptyList())
    val relatedBooks: StateFlow<List<Book>> = _relatedBooks.asStateFlow()

    fun loadRelatedBooks(category: BookCategories, excludeKey: String) {
        viewModelScope.launch {
            Log.d("MyLog", "➡️ loadRelatedBooks: category=$category, exclude=$excludeKey")
            getRelatedBooksUseCase(category, excludeKey)
                .onSuccess { list ->
                    Log.d("MyLog", "✅ related loaded: ${list.size} шт")
                    _relatedBooks.value = list }

                .onFailure { e -> Log.e("MyLog", "related: ${e.message}") }
        }
    }
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState = _uiState.asStateFlow()

  private fun insertRating(ratingData: RatingData, bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = booksRepo.submitUserRating(ratingData, bookId)
            result.fold(
                onSuccess = {
                    _uiState.value = uiState.value.copy(
                        showRateDialog = false
                    )
                },
                onFailure = { error ->

                }
            )
        }
    }

  private fun getBookComments(bookId: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = booksRepo.getBookComments(bookId)
        result.fold(
            onSuccess = { commentsList ->
                _uiState.value = uiState.value.copy(
                    comments = commentsList
                )
            },
            onFailure = { error ->

            }
        )
    }

    private fun getUserRating(bookId: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = booksRepo.getUserRating(bookId)
        result.fold(
            onSuccess = { rData ->
                _uiState.value = uiState.value.copy(
                    showRateDialog = true,
                    ratingData =  rData?: RatingData()
                )
            },
            onFailure = { error ->

            }
        )
    }

    fun onEvent(event: DetailUiEvents.DetailUiEvent) {
        when (event) {
            is DetailUiEvents.DetailUiEvent.CommentDialogEvent -> {
                _uiState.value = uiState.value.copy(
                    showCommentDialog = event.show,
                    ratingDataToShow = event.ratingData ?: RatingData()
                )
            }
            is DetailUiEvents.DetailUiEvent.ShowUserRatingDialogEvent -> {
                getUserRating(event.bookId)
            }
            is DetailUiEvents.DetailUiEvent.HideUserRatingDialog -> {
                _uiState.value = uiState.value.copy(
                    showRateDialog =false)
            }
            is DetailUiEvents.DetailUiEvent.InsertRatingDialogEvent -> {
                insertRating(event.ratingData, event.bookId)
            }
            is DetailUiEvents.DetailUiEvent.GetCommentsEvent -> {
                getBookComments(event.bookId)
            }
        }
    }

    // Выносим логику шаринга в отдельную функцию
    suspend fun sharePlace(
        context: Context,
        place: NavRoutes.ParallaxNavObject,
        coroutineScope: CoroutineScope
    ) {
        withContext(Dispatchers.IO) {
            val shareText = buildString {
                appendLine(place.title)
                appendLine()
                appendLine("⭐️ Рейтинг: ${place.ratingsList.average().format(1)}/5")
                appendLine("📍 Адрес: ${place.address}")
                appendLine("📞 Телефон: ${place.telephone}")
                appendLine("🕐 Режим работы: ${place.isOpenNow}")
                if (place.title.isNotEmpty()) {
                    appendLine("🌐 Сайт: ${place.title}")
                }
                appendLine()
                appendLine(place.description)
                appendLine()
                appendLine("Поделиться из приложения Guide Тамань")
            }

            withContext(Dispatchers.Main) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(
                    Intent.createChooser(shareIntent, "Поделиться местом")
                )
            }
        }
    }

    // Функция для форматирования числа
    fun Double.format(digits: Int) = "%.${digits}f".format(this)


}