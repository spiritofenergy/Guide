package com.kodex.guide.presentation.myPosts

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.data.images.BitmapEncoder
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.PostDraftState
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.domain.tarif.AuthStateProvider
import com.kodex.guide.domain.usecase.DeleteMyPostUseCase
import com.kodex.guide.domain.usecase.GetMyPostUseCase
import com.kodex.guide.domain.usecase.ObserveMyPostsUseCase
import com.kodex.guide.domain.usecase.SaveDraftUseCase
import com.kodex.guide.domain.usecase.UploadMyPostUseCase
import com.kodex.guide.domain.usecase.ValidatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/*sealed interface MyPostsEvent {
    // ✅ ДОБАВИТЬ ЭТО СОБЫТИЕ
    data class Toast(val message: String) : MyPostsEvent

    data class RestoreDraft(val draft: PostEditorState) : MyPostsEvent
}*/
sealed interface MyPostsEvent {
    data class Toast(val message: String) : MyPostsEvent

    // ✅ Теперь событие несет в себе готовое состояние для экрана
    data class RestoreDraft(val state: PostEditorState) : MyPostsEvent
}
@HiltViewModel
class MyPostsViewModel @Inject constructor(
    private val validatePost: ValidatePostUseCase, // ✅ Новая зависимость
    private val observeMyPosts: ObserveMyPostsUseCase,
    private val getMyPost: GetMyPostUseCase,
    private val saveDraft: SaveDraftUseCase,
    private val uploadMyPost: UploadMyPostUseCase,
    private val deleteMyPost: DeleteMyPostUseCase,
    private val bitmapEncoder: BitmapEncoder,
    private val authStateProvider: AuthStateProvider,
    private val preferenceDataSource: PreferenceDataSource, // ✅ Внедряем хранилище


) : ViewModel() {


    // ✅ Состояние для ошибок валидации
    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()

    private val _myPosts = MutableStateFlow<List<Book>>(emptyList())
    val myPosts: StateFlow<List<Book>> = _myPosts.asStateFlow()

    private val _editPost = MutableStateFlow<Book?>(null)
    val editPost: StateFlow<Book?> = _editPost.asStateFlow()

    private val _events = MutableSharedFlow<MyPostsEvent>()
    val events: SharedFlow<MyPostsEvent> = _events.asSharedFlow()

    // ✅ Метод загрузки черновика (вызывать при открытии экрана нового поста)
    fun loadSavedDraft() {
        viewModelScope.launch {
            preferenceDataSource.getPostDraft()?.let { draft ->
                // Конвертируем сохраненный Draft в состояние редактора
                // (Предполагается, что у вас есть маппер или конструктор)
                val state = PostEditorState(
                    title = draft.title,
                    description = draft.description,
                    price = draft.price.toString(),
                    telephone = draft.telephone,
                    village = draft.village,
                    street = draft.street,
                    house = draft.house,
                    flat = draft.flat,
                    category = BookCategories.entries.find { it.id == draft.categoryId } ?: BookCategories.SERVICES,
                    delivery = draft.delivery,
                    payment = draft.payment,
                    location = draft.location,
                    existingImageUrl = draft.imageUrl
                )
              //  _events.emit(MyPostsEvent.RestoreDraft(draft))
                _events.emit(MyPostsEvent.RestoreDraft(draft))

            }
        }
    }

    // ✅ Метод сохранения текущего состояния (вызывать из DisposableEffect в UI)
    fun saveCurrentAsDraft(state: PostEditorState) {
        viewModelScope.launch {
            val draft = PostDraftState(
                title = state.title,
                description = state.description,
                price = state.price.toIntOrNull() ?: 0,
                telephone = state.telephone,
                village = state.village,
                street = state.street,
                house = state.house,
                flat = state.flat,
                categoryId = state.category.id,
                delivery = state.delivery,
                payment = state.payment,
                location = state.location,
                imageUrl = state.existingImageUrl // Сохраняем Base64 или URL старого фото
            )
            preferenceDataSource.savePostDraft(draft)
        }
    }
    // ✅ Очистка после успешной публикации
    fun onPostPublishedSuccessfully() {
        viewModelScope.launch {
            preferenceDataSource.clearPostDraft()
        }
    }

    init {
        loadSavedDraftIfNew() // ✅ При создании VM пытаемся восстановить черновик
    }

    /**
     * Загружает сохраненный черновик и заполняет поля формы.
     * Вызывается один раз при инициализации или явно пользователем.
     */
    private fun loadSavedDraftIfNew() {
        viewModelScope.launch {
            // Если _editPost.value == null, значит мы создаем новый пост
            if (_editPost.value == null) {
                preferenceDataSource.getPostDraft()?.let { draft ->
                    // Эмитим событие или обновляем отдельный StateFlow для UI
                    // Для простоты здесь используем SharedFlow событий
                    _events.emit(MyPostsEvent.RestoreDraft(draft))
                }
            }
        }
    }
    /**
     * Автоматически сохраняет текущее состояние формы в черновик.
     * Можно вызывать в LaunchedEffect на каждое изменение поля
     * ИЛИ вызывать перед выходом со экрана / сворачиванием приложения.
     */
  /*  fun saveCurrentAsDraft(state: PostEditorState) {
        viewModelScope.launch {
            val draft = PostDraftState(
                title = title.value,
                description = description.value,
                price = price.intValue,
                telephone = telephone.value,
                village = village.value,
                street = street.value,
                house = house.value,
                flat = flat.value,
                categoryId = selectedCategory.value.id,
                delivery = delivery.value,
                payment = payment.value,
                location = location.value,
                imageUrl = imageBase64.value
            )
            preferenceDataSource.savePostDraft(draft)
        }
    }*/
    /**
     * Вызывается при УСПЕШНОЙ публикации. Черновик больше не нужен.
     */
   /* fun onPostPublishedSuccessfully() {
        preferenceDataSource.clearPostDraft()
    }*/
    // Добавляем новое событие
    sealed interface MyPostsEvent {
        data class Toast(val message: String) : MyPostsEvent
        data class RestoreDraft(val draft: PostDraftState) : MyPostsEvent // ✅ НОВОЕ
    }

    // Вспомогательная функция для создания объекта Book из текущих полей экрана
    // (Предполагается, что вы передадите эти данные или они есть в state)
    private fun createBookFromState(state: PostEditorState, uri: Uri?): Book {
        return Book(
            title = state.title,
            description = state.description,
            price = state.price.toIntOrNull() ?: 0,
            telephone = state.telephone,
            village = state.village,
            street = state.street,
            house = state.house,
            flat = state.flat,
            categoryIndex = state.category,
            delivery = state.delivery,
            payment = state.payment,
            location = state.location,
            imageUrl = (uri?.let { bitmapEncoder.imageToBase64(it) } ?: state.imageUri) as String

        )
    }


    // В MyPostsViewModel.kt

    fun savePost(state: PostEditorState, publish: Boolean) = viewModelScope.launch {
        // 1. Создаем базовый Book из состояния (без key и без нового фото)
        var book = state.toBook()

        // 2. Если мы в режиме редактирования, сохраняем оригинальный key и метаданные
        _editPost.value?.let { original ->
            book = book.copy(key = original.key, isUploaded = original.isUploaded)
        }
        // 2. Если есть новое фото, конвертируем его в Base64 прямо здесь
        if (state.imageUri != null) {
            val base64Image = bitmapEncoder.imageToBase64(state.imageUri)
            book = book.copy(imageUrl = base64Image)
        }
        // 3. Обрабатываем изображение (если пользователь выбрал новое)
        if (state.imageUri != null) {
            val base64Image = bitmapEncoder.imageToBase64(state.imageUri)
            book = book.copy(imageUrl = base64Image)
        }
        // 3. Вызываем UseCase валидации
        val result = validatePost(book)
        // 4. Если есть ошибки — обновляем состояние для диалога и выходим
        if (!result.isValid) {
            _validationErrors.value = result.errors
            return@launch
        }
        // 5. Если всё ок — очищаем ошибки и сохраняем
        _validationErrors.value = emptyList()

        // 4. Сохраняем
        saveDraft(book)
            .onSuccess { saved ->
                _events.emit(MyPostsEvent.Toast("Сохранено на устройстве"))
                if (publish) upload(saved)
            }
            .onFailure { _events.emit(MyPostsEvent.Toast(it.message ?: "Ошибка сохранения")) }
        // Метод для закрытия диалога
        fun clearValidationErrors() {
            _validationErrors.value = emptyList()
        }
    }
  /*  // ✅ Единый метод сохранения с валидацией
    fun savePost(state: PostEditorState, imageUri: Uri?, publish: Boolean) = viewModelScope.launch {
        val book = createBookFromState(state, imageUri)
        val result = validatePost(book)

        if (!result.isValid) {
            _validationErrors.value = result.errors
            return@launch
        }

        // Если валидация прошла успешно, очищаем ошибки и сохраняем
        _validationErrors.value = emptyList()

        saveDraft(book)
            .onSuccess { saved ->
                _events.emit(MyPostsEvent.Toast("Сохранено на устройстве"))
                if (publish) upload(saved)
            }
            .onFailure { _events.emit(MyPostsEvent.Toast(it.message ?: "Ошибка сохранения")) }
    }*/

    fun clearValidationErrors() {
        _validationErrors.value = emptyList()
    }

    // ... остальной код ...

    init {
        val uid = authStateProvider.currentUser()?.uid
        if (uid != null) {
            observeMyPosts(uid)
                .onEach { _myPosts.value = it }
                .launchIn(viewModelScope)
        }
    }

    fun loadForEdit(key: String) {
        if (key.isEmpty()) return
        viewModelScope.launch {
            getMyPost(key).onSuccess { _editPost.value = it }
        }
    }

    // ✅ сохранить черновик (и сразу опубликовать, если publish = true)
    fun save(book: Book, uri: Uri? = null, publish: Boolean = false) = viewModelScope.launch {
        val withImage = book.copy(
            imageUrl = uri?.let { bitmapEncoder.imageToBase64(it) } ?: book.imageUrl)
        saveDraft(withImage)
            .onSuccess { saved ->
                _events.emit(MyPostsEvent.Toast("Сохранено на устройстве"))
                if (publish) upload(saved) }
            .onFailure { _events.emit(MyPostsEvent.Toast(it.message ?: "Ошибка сохранения")) }
    }

    fun upload(book: Book) = viewModelScope.launch {
        uploadMyPost(book)
            .onSuccess { _events.emit(MyPostsEvent.Toast("Опубликовано")) }
            .onFailure { _events.emit(MyPostsEvent.Toast("Ошибка публикации: ${it.message}")) }
    }

    fun delete(book: Book) = viewModelScope.launch {
        deleteMyPost(book)
            .onSuccess { _events.emit(MyPostsEvent.Toast("Удалено")) }
            .onFailure { _events.emit(MyPostsEvent.Toast(it.message ?: "Ошибка удаления")) }
    }
}