package com.kodex.guide.presentation.banners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(
    private val bannerRepo: com.kodex.guide.domain.repository.BannerRepo,
    private val booksRepo: com.kodex.guide.domain.repository.BooksRepo,
    private val authStateProvider: com.kodex.guide.domain.tarif.AuthStateProvider,
    private val preferenceDataSource: com.kodex.guide.data.source.local.PreferenceDataSource
) : ViewModel() {

    private val TAG = "BannerViewModel"

    val activeBanners = bannerRepo.observeActiveBanners()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    // 1. Создаем изменяемый StateFlow
    private val _myPosts = MutableStateFlow<List<Book>>(emptyList())
    val myPosts: StateFlow<List<Book>> = _myPosts.asStateFlow()

    init {
        loagdMyPosts()
    }

    private fun loagdMyPosts() {
        val user = authStateProvider.currentUser() ?: run {
            android.util.Log.e(TAG, "Пользователь не авторизован, не можем загрузить посты")
            return

        }
        viewModelScope.launch {
            try {
                // ВНИМАНИЕ: Замените getUserBooksFlow на реальный метод из вашего BooksRepo,
                // который возвращает Flow<List<Book>> или List<Book> для конкретного пользователя.
                // Например: booksRepo.observeUserBooks(user.uid) или booksRepo.getMyBooks(user.uid)

                booksRepo.getMyPostsLocal(user.uid).collect { books ->
                    _myPosts.value = books
                }

                // ЕСЛИ ваш метод возвращает просто List (suspend function), а не Flow, используйте так:
                // val books = booksRepo.getMyBooksList(user.uid)
                // _myPosts.value = books

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Ошибка загрузки постов из Room", e)
            }
        }
    }

        fun saveBanner(
            postKey: String,
            imageUrl: String,
            publishDate: Long = System.currentTimeMillis(),
            expireDate: Long = 0,
            categoryIndex: Int = 0,
            phone: String = "",
            textTopLeft: String = "",
            textTopRight: String = "",
            textBottomLeft: String = "",
            textBottomRight: String = ""
        ) {
            val user = authStateProvider.currentUser() ?: run {
                android.util.Log.e(TAG, "Пользователь не авторизован")
                return
            }

            android.util.Log.d(TAG, "Сохранение баннера: postKey=$postKey")

            val userRole = preferenceDataSource.getUser()?.role ?: com.kodex.guide.domain.model.UserRole.USER
            val banner = com.kodex.guide.domain.model.Banner(
                id = UUID.randomUUID().toString(),
                postKey = postKey,
                imageUrl = imageUrl,
                authorUid = user.uid,
                authorRole = userRole.level,
                publishDate = publishDate,
                expireDate = expireDate,
                categoryIndex = categoryIndex,
                phone = phone,
                textTopLeft = textTopLeft,
                textTopRight = textTopRight,
                textBottomLeft = textBottomLeft,
                textBottomRight = textBottomRight
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

        fun deleteBanner(id: String) {
            viewModelScope.launch {
                bannerRepo.deleteBanner(id)
                    .onSuccess {
                        android.util.Log.d(TAG, "Баннер успешно удалён: $id")
                    }
                    .onFailure { error ->
                        android.util.Log.e(TAG, "Ошибка удаления баннера", error)
                    }
            }
        }
    }

