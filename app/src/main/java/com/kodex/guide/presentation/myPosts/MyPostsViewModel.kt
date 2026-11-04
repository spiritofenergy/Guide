package com.kodex.guide.presentation.myPosts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.data.images.BitmapEncoder
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.PostSampleState
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.domain.model.toEditorState
import com.kodex.guide.domain.tarif.AuthStateProvider
import com.kodex.guide.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first


@HiltViewModel
class MyPostsViewModel @Inject constructor(
    private val validatePost: ValidatePostUseCase,
    private val geocodePostAddress: GeocodePostAddressUseCase,
    private val observeMyPosts: ObserveMyPostsUseCase,
    private val getMyPost: GetMyPostUseCase,
    private val uploadMyPost: UploadMyPostUseCase,
    private val deleteMyPost: DeleteMyPostUseCase,
    private val saveMyPost: SaveMyPostUseCase,
    private val bitmapEncoder: BitmapEncoder,
    private val authStateProvider: AuthStateProvider,
    private val observeLatestSample: ObserveLatestSampleUseCase

) : ViewModel() {

    // Ошибки валидации
    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()

    private val _isSavingOrUploading = MutableStateFlow(false)
    val isSavingOrUploading: StateFlow<Boolean> = _isSavingOrUploading.asStateFlow()
    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()


    // Список моих постов
    private val _myPosts = MutableStateFlow<List<Book>>(emptyList())
    val myPosts: StateFlow<List<Book>> = _myPosts.asStateFlow()

    // Пост, который мы редактируем (если key не пустой)
    private val _editPost = MutableStateFlow<Book?>(null)
    val editPost: StateFlow<Book?> = _editPost.asStateFlow()

    // Поток событий UI (тосты, навигация)
    private val _uiEvents = MutableSharedFlow<MyPostsEvent>(replay = 1)
    val uiEvents: SharedFlow<MyPostsEvent> = _uiEvents.asSharedFlow()

    init {
        loadMyPosts()
    }


    private fun loadMyPosts() {
        val uid = authStateProvider.currentUser()?.uid ?: return
        viewModelScope.launch {
            observeMyPosts(uid)
                .onEach { _myPosts.value = it }
                .launchIn(viewModelScope)
        }
    }

    fun loadForEdit(key: String) {
        if (key.isEmpty()) return
        viewModelScope.launch {
            getMyPost(key).onSuccess {
                _editPost.value = it
            }.onFailure {
                emitToast("Ошибка загрузки: ${it.message}") } } }


    /**
     * Вызывается с экрана, когда bookKey пуст (создание нового объявления).
     * Берёт последний сохранённый образец и отправляет его в UI через RestoreSample.
     */
    fun loadLatestSampleForNewPost() {
        viewModelScope.launch {
            val uid = authStateProvider.currentUser()?.uid ?: return@launch
            val latest = observeLatestSample(uid) ?: return@launch

            // val latest = observeMyPosts(uid).first().firstOrNull() ?: return@launch
            _uiEvents.emit(MyPostsEvent.RestoreSample(latest.toEditorState()))
        }
    }

    fun savePost(state: PostEditorState, publish: Boolean) {
        if (_isSavingOrUploading.value) return
        _isSavingOrUploading.value = true
        _isPublishing.value = publish

        viewModelScope.launch {
            try {
                // ---------- 1. Собираем Book из состояния формы ----------
                var book = state.toBook()

                // При редактировании сохраняем key существующего поста,
                // чтобы saveDraft сделал upsert, а не создал дубликат.
                _editPost.value?.let { original ->
                    book = book.copy(
                        key = original.key,
                        isUploaded = original.isUploaded
                    )
                }

                // ---------- 2. Фото ----------
                if (state.imageUri != null) {
                    val base64Image = bitmapEncoder.imageToBase64(state.imageUri)
                    book = book.copy(imageUrl = base64Image)
                } else if (state.existingImageUrl.isNotEmpty()) {
                    book = book.copy(imageUrl = state.existingImageUrl)
                }

                // ---------- 3. Валидация ----------
                val result = validatePost(book)
                if (!result.isValid) {
                    _validationErrors.value = result.errors
                    emitToast("Проверьте заполнение полей")
                    return@launch
                }
                _validationErrors.value = emptyList()

                // ---------- 4. Геокодинг (если включён показ на карте) ----------
                if (state.location) {
                    val coordinates = geocodePostAddress(
                        state.village, state.street, state.house
                    )
                    if (coordinates == null) {
                        android.util.Log.w("MyPostDebug", "Geocode failed")

                        _validationErrors.value = listOf(
                            "Не удалось определить координаты по адресу. " +
                                    "Проверьте населённый пункт, улицу и дом."
                        )
                        emitToast("Не удалось определить координаты по адресу")
                        return@launch
                    }
                    book = book.copy(
                        latitude = coordinates.latitude.toString(),
                        longitude = coordinates.longitude.toString()
                    )
                } else {
                    book = book.copy(latitude = null, longitude = null)
                }

                // ---------- 5. ВСЕГДА сначала сохраняем в Room ----------
                android.util.Log.d("MyPostDebug", "saveSample: book=$book")
                // saveDraft вернёт Book с уже сгенерированным key и isUploaded = false.
                val saveResult = saveMyPost(book)
                android.util.Log.d("MyPostDebug", "saveResult=$saveResult")

                if (saveResult.isFailure) {
                    emitToast("Ошибка сохранения: ${saveResult.exceptionOrNull()?.message}")
                    return@launch
                }
                val saved = saveResult.getOrThrow()

                // ---------- 6. Ветвление по кнопке ----------
                if (!publish) {
                    // «Сохранить» → только Room, статус «Образец»
                    emitToast("Объявление сохранено")
                    _uiEvents.emit(MyPostsEvent.NavigateBack)
                } else {
                    // «Отправить» → Room уже сохранён, теперь Firebase (+ баннер, если включён)
                    uploadMyPost(saved)
                        .onSuccess {
                            emitToast("Объявление опубликовано!")
                            _uiEvents.emit(MyPostsEvent.NavigateBack)
                        }
                        .onFailure {
                            // Черновик уже в Room — пользователь вернётся в список
                            // и сможет дослать кнопкой «Опубл.» на карточке.
                            emitToast(
                                "Ошибка отправки: ${it.message}. Сохранено локально"
                            )
                            _uiEvents.emit(MyPostsEvent.NavigateBack)
                        }
                }
            } catch (error: Exception) {
                emitToast(
                    if (publish) "Ошибка публикации: ${error.message}"
                    else "Ошибка сохранения: ${error.message}"
                )
            } finally {
                _isSavingOrUploading.value = false
                _isPublishing.value = false
            }
        }
    }


    fun upload(book: Book) {
        viewModelScope.launch {
            uploadMyPost(book)
                .onSuccess { emitToast("Объявление опубликовано!") }
                .onFailure { emitToast("Ошибка публикации: ${it.message}") }
        }
    }

    fun delete(book: Book) {
        viewModelScope.launch {
            deleteMyPost(book)
                .onSuccess { emitToast("Объявление удалено") }
                .onFailure { emitToast("Ошибка удаления: ${it.message}") }
        }
    }

    fun clearValidationErrors() {
        _validationErrors.value = emptyList()
    }
    private suspend fun emitToast(message: String) {
        _uiEvents.emit(MyPostsEvent.Toast(message))
    }

}