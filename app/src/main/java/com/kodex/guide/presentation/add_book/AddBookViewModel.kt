package com.kodex.guide.presentation.add_book

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.bookmarketcompose.R
import com.kodex.guide.data.images.BitmapEncoder
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.data.source.remote.FirebaseAuthDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.PostDraftState
import com.kodex.guide.domain.repository.BooksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val booksRepo: BooksRepo,
    private val bitmapEncoder: BitmapEncoder,
    private val authRepository: FirebaseAuthDataSource, // или UserSession
    private val preferenceDataSource: PreferenceDataSource, // ✅ Внедряем хранилище

) : ViewModel() {

    val title = mutableStateOf("")
    val village = mutableStateOf("")
    val description = mutableStateOf("")
    val price = mutableIntStateOf(50)
    val telephone = mutableStateOf("")
    val location = mutableStateOf(false)
    val street = mutableStateOf("")
    val flat = mutableStateOf("")
    val house = mutableStateOf("")
    val delivery = mutableStateOf(false)
    val payment = mutableStateOf(false)
    var imageBase64 = mutableStateOf("")

    val validationError = mutableStateOf<String?>(null)

    val selectedCategory = mutableStateOf(BookCategories.ALL)
    val selectedImageUri = mutableStateOf<Uri?>(null)
    val showLoadingIndicator = mutableStateOf(false)

    private val _uiState = MutableSharedFlow<HomeViewModel.MainUiState>()
    val uiState = _uiState.asSharedFlow()

    private fun sendUiState(state: HomeViewModel.MainUiState) = viewModelScope.launch {
        _uiState.emit(state)
    }

    // Добавьте метод для конвертации URI в Base64
    fun convertImageToBase64(uri: Uri): String {
        return bitmapEncoder.imageToBase64(uri)
    }
    init {
        loadSavedDraft() // ✅ При создании VM пытаемся восстановить черновик
    }
    /**
     * Загружает сохраненный черновик и заполняет поля формы.
     * Вызывается один раз при инициализации или явно пользователем.
     */
    private fun loadSavedDraft() {
        viewModelScope.launch {
            preferenceDataSource.getPostDraft()?.let { draft ->
                title.value = draft.title
                description.value = draft.description
                price.intValue = draft.price
                telephone.value = draft.telephone
                village.value = draft.village
                street.value = draft.street
                house.value = draft.house
                flat.value = draft.flat
                selectedCategory.value = BookCategories.fromId(draft.categoryId)
                delivery.value = draft.delivery
                payment.value = draft.payment
                location.value = draft.location
                imageBase64.value = draft.imageUrl

                // Если есть сохраненное фото, конвертируем его обратно в Uri для отображения
                if (draft.imageUrl.isNotEmpty()) {
                    // Логика восстановления Uri из Base64 если нужна
                }
            }
        }
    }
    /**
     * Автоматически сохраняет текущее состояние формы в черновик.
     * Можно вызывать в LaunchedEffect на каждое изменение поля
     * ИЛИ вызывать перед выходом со экрана / сворачиванием приложения.
     */
    fun saveCurrentAsDraft() {
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
    }
    /**
     * Вызывается при УСПЕШНОЙ публикации. Черновик больше не нужен.
     */
    fun onPostPublishedSuccessfully() {
        preferenceDataSource.clearPostDraft()
    }

    // Функция валидации
    fun validateBook(context: Context): Boolean {
        val errors = mutableListOf<String>()
        if (title.value.isBlank()) { errors.add(context.getString(R.string.title_is_required)) }
        if (description.value.isBlank()) { errors.add(context.getString(R.string.description_is_required)) }
        if (village.value.isBlank()) { errors.add(context.getString(R.string.please_specify_the_village)) }
        if (price.intValue <= 0) { errors.add(context.getString(R.string.enter_a_valid_price)) }
        // 🔍 Проверка фото
        if (selectedImageUri.value == null && imageBase64.value.isBlank()) {
            errors.add(context.getString(R.string.please_add_a_photo))
        }
        if (errors.isNotEmpty()) {
            validationError.value = errors.joinToString("\n")
            return false
        }
        validationError.value = null
        return true
    }

    // Сброс ошибки (вызывать при закрытии диалога)
    fun clearValidationError() {
        validationError.value = null
    }
    fun setDefaultData(navData: NavRoutes.AddScreenObject) {
        Log.d("EditDebug", "Пришло на экран: village=${navData.village}, delivery=${navData.delivery}, payment=${navData.payment}")

        title.value = navData.title
        description.value = navData.description
        price.intValue = navData.price
        telephone.value = navData.telephone
        selectedCategory.value = navData.categoryIndex
        village.value = navData.village
        street.value = navData.village
        house.value = navData.house
        flat.value = navData.flat
        location.value = navData.location
        delivery.value = navData.delivery
        payment.value = navData.payment

        // ✅ Если есть фото в navData, сохраняем его
        if (navData.imageUrl.isNotEmpty()) {
            imageBase64.value = navData.imageUrl
        }
    }

    fun uploadBook(book: Book, ) {
        sendUiState(HomeViewModel.MainUiState.Loading)
        viewModelScope.launch {
            val result = booksRepo.saveBook(
                book.copy(
                    title = title.value,
                    description = description.value,
                    price = price.intValue,
                    village = village.value,
                    categoryIndex = selectedCategory.value,
                    delivery = delivery.value,
                    payment = payment.value,
                    imageUrl = imageBase64.value // ✅ ДОБАВЛЕНО
                ),

                selectedImageUri.value)
            result.fold(
                onSuccess = { sendUiState(HomeViewModel.MainUiState.Success) },
                onFailure = { error-> sendUiState(HomeViewModel.MainUiState.Error(error.message ?: "Unknow error"))
                }
            )
        }
    }
}