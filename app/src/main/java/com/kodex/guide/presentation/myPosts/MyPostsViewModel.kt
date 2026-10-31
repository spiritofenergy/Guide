package com.kodex.guide.presentation.myPosts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.data.images.BitmapEncoder
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.PostSampleState
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.domain.tarif.AuthStateProvider
import com.kodex.guide.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPostsViewModel @Inject constructor(
    private val validatePost: ValidatePostUseCase,
    private val geocodePostAddress: GeocodePostAddressUseCase,
    private val observeMyPosts: ObserveMyPostsUseCase,
    private val getMyPost: GetMyPostUseCase,
    private val uploadMyPost: UploadMyPostUseCase,
    private val deleteMyPost: DeleteMyPostUseCase,
    private val bitmapEncoder: BitmapEncoder,
    private val authStateProvider: AuthStateProvider,
   // private val observeLatestDraft: ObserveLatestDraftUseCase
        val preferenceDataSource : PreferenceDataSource
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
    private val _uiEvents = MutableSharedFlow<MyPostsEvent>()
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
                emitToast("Ошибка загрузки: ${it.message}")
            }
        }
    }

    fun savePost(state: PostEditorState, publish: Boolean) {
        if (_isSavingOrUploading.value) return
        _isSavingOrUploading.value = true
        _isPublishing.value = publish

        viewModelScope.launch {
            try {
                var book = state.toBook()
                _editPost.value?.let { original ->
                    book = book.copy(
                        key = original.key,
                        isUploaded = original.isUploaded
                    )
                }
                if (state.imageUri != null) {
                    val base64Image = bitmapEncoder.imageToBase64(state.imageUri)
                    book = book.copy(imageUrl = base64Image)
                } else if (state.existingImageUrl.isNotEmpty()) {
                    book = book.copy(imageUrl = state.existingImageUrl)
                }
                val result = validatePost(book)
                if (!result.isValid) {
                    _validationErrors.value = result.errors
                    emitToast("Проверьте заполнение полей")
                    return@launch
                }
                _validationErrors.value = emptyList()

                if (state.location) {
                    val coordinates = geocodePostAddress(state.village, state.street, state.house)
                    if (coordinates == null) {
                        _validationErrors.value = listOf(
                            "Не удалось определить координаты по адресу. Проверьте населённый пункт, улицу и дом."
                        )
                        emitToast("Не удалось определить координаты по адресу")
                        return@launch
                    }
                    book = book.copy(
                        latitude = coordinates.latitude.toString(),
                        longitude = coordinates.longitude.toString()
                    )
                } else if (!state.location) {
                    book = book.copy(latitude = null, longitude = null)
                }

                if (!publish) {
                    // ✅ Сохраняем образец без фото
                    try {
                        val sampleAd = PostSampleState(
                            title = book.title,
                            description = book.description,
                            price = book.price,
                            telephone = book.telephone,
                            village = book.village,
                            street = book.street,
                            house = book.house,
                            flat = book.flat,
                            categoryIndex = book.categoryIndex,
                            delivery = book.delivery,
                            payment = book.payment,
                            location = book.location,
                        )
                        preferenceDataSource.saveSampleAd(sampleAd)
                        emitToast("Объявление сохранено")
                        _uiEvents.emit(MyPostsEvent.NavigateBack)
                    } catch (e: Exception) {
                        emitToast("Ошибка сохранения: ${e.message}")
                    }
                }
                else {
                    uploadMyPost(book)
                        .onSuccess {
                            emitToast("Объявление опубликовано!")
                            _uiEvents.emit(MyPostsEvent.NavigateBack)
                        }
                        .onFailure {
                            emitToast("Ошибка публикации: ${it.message}")
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
            val state = PostEditorState(
                title = book.title,
                description = book.description,
                price = book.price.toString(),
                telephone = book.telephone,
                village = book.village,
                street = book.street,
                house = book.house,
                flat = book.flat,
                category = book.categoryIndex,
                delivery = book.delivery,
                payment = book.payment,
                location = book.location,
                imageUri = null,
                existingImageUrl = book.imageUrl
            )
            _editPost.value = book
           // savePost(state = state, publish = true)
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