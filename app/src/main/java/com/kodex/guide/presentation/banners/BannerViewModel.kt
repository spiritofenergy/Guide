package com.kodex.guide.presentation.banners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(
    private val bannerRepo: com.kodex.guide.domain.repository.BannerRepo,
    private val booksRepo: com.kodex.guide.domain.repository.BooksRepo,
    private val authStateProvider: com.kodex.guide.domain.tarif.AuthStateProvider
) : ViewModel() {

    private val TAG = "BannerViewModel"

    val activeBanners = bannerRepo.observeActiveBanners()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Список своих объявлений - загружаем быстро из локального кэша
    // Список своих объявлений - пока пустой, TODO: добавить локальный запрос
    val myPosts: StateFlow<List<Book>> = MutableStateFlow<List<Book>>(emptyList())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun saveBanner(postKey: String, imageUrl: String) {
        val user = authStateProvider.currentUser() ?: run {
            android.util.Log.e(TAG, "Пользователь не авторизован")
            return
        }
        
        android.util.Log.d(TAG, "Сохранение баннера: postKey=$postKey, imageUrl=$imageUrl")
        
        val banner = com.kodex.guide.domain.model.Banner(
            id = UUID.randomUUID().toString(),
            postKey = postKey,
            imageUrl = imageUrl,
            authorUid = user.uid
        )

        viewModelScope.launch {
            bannerRepo.saveBanner(banner)
                .onSuccess {
                    android.util.Log.d(TAG, "Баннер успешно сохранён: ${banner.id}")
                }
                .onFailure { error ->
                    android.util.Log.e(TAG, "Ошибка сохранения баннера", error)
                }
        }
    }
}
