package com.kodex.guide.presentation.detailScreen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.data.repository.BooksRepo_Impl
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.domain.usecase.GetRelatedBooksUseCase
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
    val booksRepo: BooksRepo_Impl,
    private val getRelatedBooksUseCase: GetRelatedBooksUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ✅ Достаем "bookId", так как в NavRoutes он называется bookId
    private val bookId: String = savedStateHandle["bookId"] ?: ""

    // Состояние для похожих постов
    private val _relatedBooks = MutableStateFlow<List<Book>>(emptyList())
    val relatedBooks: StateFlow<List<Book>> = _relatedBooks.asStateFlow()

    // Основное состояние экрана
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("DetailsDebug", "VM создана. bookId = '$bookId'")
        if (bookId.isNotEmpty()) {
            loadBookDetails()
        }
    }

    private fun loadBookDetails() {
        viewModelScope.launch {
            // 1. Локально
            val localBook = booksRepo.getBookFromLocal(bookId)
            if (localBook != null) {
                _uiState.value = _uiState.value.copy(book = localBook)
            }

            // 2. Из сети
            val remoteBookResult = booksRepo.getBookFromRemote(bookId)
            remoteBookResult.onSuccess { remoteBook ->
                if (remoteBook != null) {
                    _uiState.value = _uiState.value.copy(book = remoteBook)
                }
            }.onFailure {
                Log.e("DetailsDebug", "Ошибка загрузки книги: ${it.message}")
            }
        }
    }

    fun loadRelatedBooks(category: BookCategories, excludeKey: String) {
        viewModelScope.launch {
            Log.d("MyLog", "➡️ loadRelatedBooks: category=$category, exclude=$excludeKey")
            getRelatedBooksUseCase(category, excludeKey)
                .onSuccess { list ->
                    Log.d("MyLog", "✅ related loaded: ${list.size} шт")
                    _relatedBooks.value = list
                }
                .onFailure { e ->
                    Log.e("MyLog", "related: ${e.message}")
                }
        }
    }

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
                    Log.e("DetailsDebug", "Ошибка при отправке рейтинга: ${error.message}")
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
                Log.e("DetailsDebug", "Ошибка загрузки комментов: ${error.message}")
            }
        )
    }

    private fun getUserRating(bookId: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = booksRepo.getUserRating(bookId)
        result.fold(
            onSuccess = { rData ->
                _uiState.value = uiState.value.copy(
                    showRateDialog = true,
                    ratingData = rData ?: RatingData()
                )
            },
            onFailure = { error ->
                Log.e("DetailsDebug", "Ошибка загрузки рейтинга: ${error.message}")
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
                    showRateDialog = false
                )
            }
            is DetailUiEvents.DetailUiEvent.InsertRatingDialogEvent -> {
                insertRating(event.ratingData, event.bookId)
            }
            is DetailUiEvents.DetailUiEvent.GetCommentsEvent -> {
                getBookComments(event.bookId)
            }
        }
    }

    suspend fun sharePlace(
        context: Context,
        book: Book,
        coroutineScope: CoroutineScope
    ) {
        withContext(Dispatchers.IO) {
            val shareText = buildString {
                appendLine(book.title)
                appendLine()
                appendLine("⭐️ Рейтинг: ${book.ratingsList.average().format(1)}/5")
                appendLine("📍 Адрес: ${book.address}")
                appendLine("📞 Телефон: ${book.telephone}")
                appendLine("🕐 Режим работы: ${book.openingHours}")
                if (book.title.isNotEmpty()) {
                    appendLine("🌐 Сайт: ${book.title}")
                }
                appendLine()
                appendLine(book.description)
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

    fun Double.format(digits: Int) = "%.${digits}f".format(this)
}