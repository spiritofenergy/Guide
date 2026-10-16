package com.kodex.guide.presentation.home

import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kodex.guide.data.mapper.toDTO
import com.kodex.guide.data.mapper.toUser
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.data.source.remote.FirebaseAuthDataSource
import com.kodex.guide.data.repository.UserAccessRepoImpl
import com.kodex.guide.domain.repository.BooksRepo
import com.kodex.guide.domain.repository.FavoritesRepo
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.FilterData
import com.kodex.guide.ui.db.MainDb
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.domain.model.FilterType
import com.kodex.guide.domain.model.Permission
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.domain.repository.UserRoleRepo
import com.kodex.guide.domain.role.RolePermissionChecker
import com.kodex.guide.domain.tarif.ApplyRoleResult
import com.kodex.guide.domain.tarif.AuthStateProvider
import com.kodex.guide.domain.tarif.TariffPolicy
import com.kodex.guide.domain.tarif.UpgradeDecision
import com.kodex.guide.domain.tarif.UpgradeManager
import com.kodex.guide.domain.usecase.ObserveSavedKeysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.text.Typography.registered

// ✅ События для UI (диалоги, навигация)
sealed class AuthEvent {
    data object ShowLogInDialog : AuthEvent()
    data object ShowLogOutDialog : AuthEvent()
    data class NavigateToRegistration(val message: String) : AuthEvent()
    data class NavigateToSignIn(val message: String) : AuthEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val booksRepo: BooksRepo,
    private val favoritesRepo: FavoritesRepo,
    private val authDataSource: FirebaseAuthDataSource,
    private val mainDb: MainDb,
    private val preferenceDataSource: PreferenceDataSource,
    private val userRoleRepository: UserRoleRepo,
    private val rolePermissionChecker: RolePermissionChecker,
    private val tariffPolicy: TariffPolicy,
    private val upgradeManager: UpgradeManager,
    private val authStateProvider: AuthStateProvider,
    private val userAccessRepository: UserAccessRepoImpl,
    private val observeSavedKeysUseCase: ObserveSavedKeysUseCase,
    ) : ViewModel() {
    val showPaymentSheet = mutableStateOf(false)
    val paymentInProgress = mutableStateOf(false)
    val minPriceValue = mutableFloatStateOf(0f)
    val maxPriceValue = mutableFloatStateOf(0f)
    val isFilterByTitle = mutableStateOf(true)
    var showTabOneOrTo = mutableStateOf(false)
    val selectedBottomItemState = mutableIntStateOf(BottomMenuItem.Home.titleId)

    // ✅ профиль для DrawerHeader
    val headerUser = mutableStateOf<User?>(null)


    // Состояние для диалога
    val showAuthDialog = mutableStateOf(false)
    val isLogoutDialog = mutableStateOf(false)
    var isRegisterState = mutableStateOf(false)
    val categoryState = mutableStateOf(BookCategories.ALL)
    val hasSelectedCategory = mutableStateOf(false)
    private val bookListUpdate = MutableStateFlow<List<ChangedTempBook>>(emptyList())
    private val bookFilterStateFlow = MutableStateFlow(BookFilterState())
    private val searchStateFlow = MutableStateFlow("")
    private val booksRefreshFlow = MutableStateFlow(0)
    private val debounceSearchFlow = searchStateFlow
        .debounce(500)
        .distinctUntilChanged()

    // ✅ Желательный тариф для установки после регистрации
    val desiredRole = mutableStateOf<UserRole?>(null)
    val bookToDelete = mutableStateOf<Book?>(null)

    // ✅ Показ бокового меню
    private val _showDrawer = MutableStateFlow(preferenceDataSource.getShowDrawer())
    val showDrawer: StateFlow<Boolean> = _showDrawer.asStateFlow()

    fun setShowDrawer(show: Boolean) {
        _showDrawer.value = show
        preferenceDataSource.saveShowDrawer(show)
    }

    fun onDeleteRequested(book: Book) {
        bookToDelete.value = book
    }
    fun onDeleteDialogDismissed() {
        bookToDelete.value = null
    }
    private val savedKeysFlow: StateFlow<Set<String>> = observeSavedKeysUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )
    val books: Flow<PagingData<Book>> = combine(
        savedKeysFlow,
        bookFilterStateFlow,
        debounceSearchFlow,
        booksRefreshFlow
    ) { savedKeys, filter, searchText, _ ->

        Triple(
            savedKeys.toList(),   // ← важно: Set<String> -> List<String>
            filter,
            searchText
        )
    }.flatMapLatest { (keysList, filter, searchText) ->

        booksRepo.getBooks(
            keysList,
            filter.copy(searchText = searchText)
        )

    }.cachedIn(viewModelScope)
        .combine(bookListUpdate) { pagingData, changedBookList ->
            pagingData.filter { pData ->
                val book = changedBookList.find { it.key == pData.key }
                if (book != null) {
                    !book.isDeleted
                } else {
                    true
                }
            }.map { pData ->
                val book = changedBookList.find { it.key == pData.key }
                if (book != null) {
                    pData.copy(
                        isFavorite = book.isFavorite
                    )
                } else {
                    pData
                }
            }
        }

    fun refreshBooks() {
        booksRefreshFlow.update { it + 1 }
    }

    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents = _authEvents.asSharedFlow()
    private val _uiState = MutableSharedFlow<MainUiState>()
    val uiState = _uiState.asSharedFlow()

    private val _ratingsByBook = MutableStateFlow<Map<String, List<Int>>>(emptyMap())
    val ratingsByBook: StateFlow<Map<String, List<Int>>> = _ratingsByBook.asStateFlow()
    private val ratingsJobs = mutableMapOf<String, Job>()

    fun loadRatingsForBooks(books: List<Book>) {
        books.asSequence()
            .map(Book::key)
            .filter(String::isNotBlank)
            .distinct()
            .filterNot { bookId ->
                _ratingsByBook.value.containsKey(bookId) || ratingsJobs.containsKey(bookId)
            }
            .forEach { bookId ->
                ratingsJobs[bookId] = viewModelScope.launch {
                    try {
                        booksRepo.getBookComments(bookId).fold(
                            onSuccess = { comments ->
                                _ratingsByBook.update { ratings ->
                                    ratings + (bookId to comments.mapNotNull { it.rating })
                                }
                            },
                            onFailure = { error ->
                                Log.e("HomeViewModel", "Failed to load ratings for $bookId", error)
                            }
                        )
                    } finally {
                        ratingsJobs.remove(bookId)
                    }
                }
            }
    }

    // ===== Роль и авторизация — только StateFlow =====
    private val _userRole = MutableStateFlow(UserRole.ANONYMOUS)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _isAdminState = MutableStateFlow(false)
    val isAdminState: StateFlow<Boolean> = _isAdminState.asStateFlow()

    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    // ... где-то в функции обновления ...
    fun updateRole(role: UserRole) {
        // 4. Обновляем значение через .value
        _userRole.value = role
        _isAdminState.value = role == UserRole.ADMIN
        _isAuthorized.value = true   // ✅ Правильно
    }

    // ✅ Есть ли сохранённый email (пользователь уже входил/регистрировался)
    fun getSavedEmail(): String? {
        val email = preferenceDataSource.getEmail(PreferenceDataSource.EMAIL_KEY, "")
        return email.ifEmpty { null }
    }

    fun hasSavedEmail(): Boolean = getSavedEmail() != null

    // ✅ Умная навигация: email есть → SignIn, нет → SignUp
    fun navigateToAuth() = viewModelScope.launch {
        if (hasSavedEmail()) {
            Log.d("MyLog", "Email найден → открываем SignIn")
            _authEvents.emit(AuthEvent.NavigateToSignIn("Войдите в аккаунт"))
        } else {
            Log.d("MyLog", "Email не найден → открываем SignUp")
            _authEvents.emit(AuthEvent.NavigateToRegistration("Для доступа требуется регистрация"))
        }

       /*! fun getSavedEmail(): String? {
            val email = preferenceDataSource.getEmail(PreferenceDataSource.EMAIL_KEY, "")
            return email.ifEmpty { null }
        }*/
    }
    // ✅ Решает, что показать — регистрацию или оплату
    fun requestUpgrade(currentRole: UserRole) {
        when (val decision = upgradeManager.decideUpgrade(currentRole)) {
            is UpgradeDecision.MaxRole -> {
                Log.d("MyLog", "Уже максимальный тариф")
            }
            is UpgradeDecision.AuthRequired -> navigateToAuth()
            is UpgradeDecision.PaymentRequired -> showPaymentSheet.value = true
        }
    }

    // ✅ Прямой переход на ПРЕМИУМ, минуя BUSINESS
    fun requestPremiumUpgrade() {
        when (val decision = upgradeManager.decidePremium()) {
            is UpgradeDecision.MaxRole -> {
                Log.d("MyLog", "Уже максимальный тариф")
            }
            is UpgradeDecision.AuthRequired -> navigateToAuth()
            is UpgradeDecision.PaymentRequired -> showPaymentSheet.value = true
        }
    }

   /*! // ✅ НОВОЕ: прямой переход на ПРЕМИУМ, минуя BUSINESS
    fun requestPremiumUpgrade(onNavigateToRegistration: () -> Unit) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || currentUser.isAnonymous) {
            // Аноним: сначала регистрация, оплата после неё
            desiredRole.value = UserRole.PREMIUM
            navigateToAuth()
        } else {
            // Зарегистрирован — сразу оплата
            desiredRole.value = UserRole.PREMIUM   // ✅ цель оплаты
            showPaymentSheet.value = true
        }
    }*/

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val currentUser = auth.currentUser
        Log.d("ROLE_DEBUG", "authStateListener: user=$currentUser, uid=${currentUser?.uid}")

        refreshAccessFlags()
        if (currentUser == null) {
            roleJob?.cancel()
            return@AuthStateListener
        }
        // ✅ Сохраняем email: в следующий раз предложим SignIn вместо SignUp
        currentUser.email?.takeIf { it.isNotEmpty() }?.let { email ->
            preferenceDataSource.saveEmail(PreferenceDataSource.EMAIL_KEY, email)
            Log.d("MyLog", "Email сохранён: $email")
        }

        Firebase.firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                Log.d("ROLE_DEBUG", "authStateListener: doc.exists=${doc.exists()}, data=${doc.data}")

                if (!doc.exists()) {
                    // ✅ Используем маппер вместо хардкода
                    val user = currentUser.toUser(role = UserRole.USER)
                    createUserProfile(user)
                }
                // ✅ НОВОЕ: имя берём из Firestore → displayName → email
                val nameFromFirestore = doc?.getString("userName").orEmpty()
                val nameFromAuth = currentUser.displayName.orEmpty()
                val nameFromEmail = currentUser.email?.substringBefore("@").orEmpty()
                val userName = nameFromFirestore
                    .ifEmpty { nameFromAuth }
                    .ifEmpty { nameFromEmail }

                // ✅ Кеш профиля для шапки с НЕпустым именем
                preferenceDataSource.saveUser(
                    currentUser.toUser(role = UserRole.USER).copy(userName = userName)
                )
                refreshHeader()   // ✅ шапка сразу покажет имя

                loadUserRole(currentUser.uid)   // внутри — saveRole + refreshHeader
                refreshAccessFlags()
                // ✅ применяем отложенный тариф после входа
                if (!currentUser.isAnonymous && desiredRole.value != null) {
                    applyDesiredRoleAfterRegistration()
                }
            }
    }

    init {
        Log.d("ROLE_DEBUG", "=== init START ===")
        Log.d("ROLE_DEBUG", "Firebase.auth.currentUser?.uid = ${Firebase.auth.currentUser?.uid}")
        Log.d("ROLE_DEBUG", "authStateProvider.currentUser()?.uid = ${authStateProvider.currentUser()?.uid}")
        Log.d("ROLE_DEBUG", "cached user = ${preferenceDataSource.getUser()}")
     //   Log.d("ROLE_DEBUG", "cached role = ${preferenceDataSource.getRole()}")

        // миграция: кеш пуст, но пользователь залогинен — заполняем кеш
        if (preferenceDataSource.getUser() == null) {
            authDataSource.getCurrentUser()?.let { preferenceDataSource.saveUser(it) }
        }
        refreshHeader()
        // Загружаем роль при старте
        authDataSource.getCurrentUser()?.let { user ->
            _userRole.value = user.role
            _isAuthorized.value = true

        }

        // ✅ ДОБАВЛЕНО: Регистрируем слушатель состояния авторизации
        Firebase.auth.addAuthStateListener(authStateListener)
        // Если уже авторизован - загружаем роль
        Firebase.auth.currentUser?.uid?.let { loadUserRole(it) }
        refreshAccessFlags()
       // observeCategoryCounts()
    }

    /**
     * Количество постов по категориям.
     */
     val _categoryCounts = MutableStateFlow<Map<BookCategories, Int>>(emptyMap())
    val categoryCounts: StateFlow<Map<BookCategories, Int>> = _categoryCounts.asStateFlow()

  /*      private fun observeCategoryCounts() {
        viewModelScope.launch {
            booksRepo.getBooks(
                emptyList(),
                BookFilterState.Category(category = BookCategories.ALL, author = null)
            )
            .map { pagingData ->
                pagingData.itemSnapshotList.items
            }
            .collect { items ->
                if (items.isNotEmpty()) {
                    val counts = items
                        .map { it.categoryIndex }
                        .groupingBy { it }
                        .eachCount()
                        .mapKeys { (key, _) -> key as BookCategories }
                    _categoryCounts.value = counts
                }
            }
        }
    }*/

    fun refreshHeader() {
        headerUser.value = preferenceDataSource.getUser()?.copy(role = _userRole.value)
    }

    private var roleJob: Job? = null

    private fun loadUserRole(uid: String) {
        Log.d("ROLE_DEBUG", "loadUserRole: START for uid=$uid")
        roleJob?.cancel()
        roleJob = viewModelScope.launch {
            userRoleRepository.observeUserRole(uid)
                .catch { error ->
                    Log.e("MyLog", "Ошибка загрузки роли: ${error.message}")
                }
                .collect { role ->
                    Log.d("ROLE_DEBUG", "loadUserRole COLLECT: role=$role (uid=$uid)")
                    val isAdmin = _isAdminState.value || role == UserRole.ADMIN
                    _isAdminState.value = isAdmin
                    val effectiveRole = if (isAdmin) UserRole.ADMIN else role
                    _userRole.value = effectiveRole
                    _isAuthorized.value = true

                    preferenceDataSource.saveRole(effectiveRole)
                    refreshHeader()
                    Log.d("ROLE_DEBUG", "loadUserRole: after saveRole → _userRole=${_userRole.value}")

                }
        }
    }

    fun createUserProfile(user: User) {
        Firebase.firestore
            .collection("users")
            .document(user.uid)
            .set(user.toDTO())
            .addOnSuccessListener {
                Log.d("MyLog", "Профиль создан для ${user.uid}")
            }
            .addOnFailureListener { e ->
                Log.e("MyLog", "Ошибка создания профиля: ${e.message}")
            }
    }

    // ✅ Оплата и повышение до платного тарифа (BUSINESS или PREMIUM — из desiredRole)
    fun upgradeToBusiness(
        uid: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            paymentInProgress.value = true
            upgradeManager.applyPaidUpgrade(uid)
                .onSuccess { role ->
                    paymentInProgress.value = false
                    applyRoleLocally(role)
                    Log.d("MyLog", "Оплата успешна: роль повышена до ${role.name}")
                    onSuccess()
                }
                .onFailure { error ->
                    paymentInProgress.value = false
                    Log.e("MyLog", "Ошибка повышения: ${error.message}")
                    onError(error.message ?: "Неизвестная ошибка")
                }
        }
    }



    // ✅ Сохранить/получить/очистить данные карты
    fun saveCardData(cardNumber: String, expiry: String) {
        preferenceDataSource.saveCardData(cardNumber, expiry)
        Log.d("MyLog", "Данные карты сохранены")
    }

    /** Возвращает номер и срок сохранённой карты */
    fun getSavedCardData(): Pair<String, String> {
        return preferenceDataSource.getSavedCardNumber() to
                preferenceDataSource.getSavedCardExpiry()
    }

    fun clearCardData() {
        preferenceDataSource.clearCardData()
    }
    fun requiresPayment(role: UserRole): Boolean = tariffPolicy.requiresPayment(role)

    fun hasPermission(permission: Permission): Boolean {
        return rolePermissionChecker.hasPermission(userRole.value, permission)
    }

    fun canAccess(requiredRole: UserRole): Boolean {
        return rolePermissionChecker.canAccess(userRole.value, requiredRole)
    }

    fun canCreatePost(): Boolean {
        return rolePermissionChecker.canCreatePost(userRole.value)
    }

    fun canModerate(): Boolean {
        return rolePermissionChecker.canModerate(userRole.value)
    }

    // ✅ ДОБАВЛЕНО: Очищаем слушатель при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        roleJob?.cancel()
        Firebase.auth.removeAuthStateListener(authStateListener)
    }


    private fun sendUiState(state: MainUiState) = viewModelScope.launch {
        _uiState.emit(state)
    }

    fun clearTempBookList() {
        bookListUpdate.value = emptyList()
    }

    private fun updateChangedBook(book: Book, isDeleted: Boolean) {
        bookListUpdate.update { list ->
            val changedBook = list.find { book.key == it.key }
            if (changedBook == null) {
                list + ChangedTempBook(
                    key = book.key,
                    isFavorite = book.isFavorite,
                    isDeleted = isDeleted
                )
            } else {
                list.map { tempBook ->
                    if (tempBook.key == book.key) {
                        tempBook.copy(
                            isFavorite = book.isFavorite,
                            isDeleted = isDeleted
                        )
                    } else {
                        tempBook
                    }
                }
            }
        }
    }

    fun setFilter() {
        val filterData = FilterData(
            minPrise = minPriceValue.floatValue.toInt(),
            maxPrise = maxPriceValue.floatValue.toInt(),
            filterType = if (isFilterByTitle.value) {
                FilterType.TITLE
            } else
                FilterType.PRICE
        )
        bookFilterStateFlow.update { filter ->
            filter.copy(
                filterData = filterData
            )
        }
    }

    fun deleteBook() {
        val book = bookToDelete.value ?: return
        sendUiState(MainUiState.Loading)
        viewModelScope.launch {
            booksRepo.deleteBook(book).fold(
                onSuccess = {
                    updateChangedBook(book, isDeleted = true)
                    bookToDelete.value = null
                    sendUiState(MainUiState.Success) },
                onFailure = { error ->
                    sendUiState(MainUiState.Error(error.message ?: "Unknown error"))
                }
            )
        }
    }

    fun searchBook(searchText: String) {
        searchStateFlow.update {
            searchText
        }
    }

    // В HomeViewModel.kt

    /**
     * Обрабатывает выбор категории из Drawer или BottomMenu.
     * Инкапсулирует логику фильтрации и синхронизации состояния нижнего меню.
     */
    fun onCategorySelected(category: BookCategories) {
        // 1. Обновляем состояние категории и применяем фильтр
        hasSelectedCategory.value = true
        categoryState.value = category
        clearTempBookList()
      //  refreshFavoritesKeys()
        bookFilterStateFlow.update { it.copy(category = category) }

        // 2. Синхронизируем состояние нижнего меню (BottomNavigation)
        selectedBottomItemState.intValue = BottomMenuItem.Home.titleId

        Log.d("MyLog", "Категория изменена на: $category")
    }

    fun getAllBooksFromCategory(category: BookCategories) {
        hasSelectedCategory.value = true
        categoryState.value = category
        clearTempBookList()

        bookFilterStateFlow.value = BookFilterState.Category(
            category = category,
            author = null
        )

    }


    fun insertPost(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        // Проверяем, есть ли уже пост в базе
        val existingPosts = mainDb.trackDao().getAllPosts().first()
        val isAlreadySaved = existingPosts.any { it.key == book.key }

        if (isAlreadySaved) {
            // Пост уже сохранен - показываем сообщение
            withContext(Dispatchers.Main) {
                _uiState.emit(MainUiState.Error("Этот пост уже сохранен"))
            }
            return@launch
        }

        // Обновляем состояние isFavorite
        val updatedBook = book.copy(isFavorite = true)
        mainDb.trackDao().insertPost(updatedBook)

        // Обновляем UI через bookListUpdate
        updateChangedBook(updatedBook, isDeleted = false)

        withContext(Dispatchers.Main) {
            _uiState.emit(MainUiState.Success)
        }
    }

    sealed class MainUiState {
        data object Loading : MainUiState()
        data object Success : MainUiState()
        data class Error(val message: String) : MainUiState()
    }

   fun applyDesiredRoleAfterRegistration() {
       viewModelScope.launch {
           when (val result = upgradeManager.applyDesiredRole()) {
               is ApplyRoleResult.NoDesiredRole -> Unit

               is ApplyRoleResult.PaymentRequired -> {
                   Log.d("MyLog", "Тариф ${result.role} требует оплаты — открываем платёжный экран")
                   showPaymentSheet.value = true
               }

               is ApplyRoleResult.Updated -> applyRoleLocally(result.role)

               is ApplyRoleResult.Failed -> {
                   Log.e("MyLog", "Ошибка обновления тарифа: ${result.message}")
                   sendUiState(MainUiState.Error(result.message))
               }
           }
       }
   }
    // ✅ Локальное применение роли: UI + кеш + шапка
    private fun applyRoleLocally(role: UserRole) {
        _userRole.value = role
        _isAuthorized.value = true
        viewModelScope.launch { preferenceDataSource.saveRole(role) }
        refreshHeader()
    }


    // ✅ Получение следующего тарифа (цепочка: ANONYMOUS → USER → BUSINESS → PREMIUM)
    fun getNextRole(currentRole: UserRole): UserRole? {
        return when (currentRole) {
            UserRole.ANONYMOUS -> UserRole.USER
            UserRole.USER -> UserRole.BUSINESS
            UserRole.BUSINESS -> UserRole.PREMIUM   // ✅ НОВОЕ
            UserRole.PREMIUM -> null               // ✅ максимум
            UserRole.ADMIN -> null
        }
    }

    // ✅ Отображаемое имя тарифа
    fun getRoleDisplayName(role: UserRole): String {
        return when (role) {
            UserRole.ANONYMOUS -> "Гостевой"
            UserRole.USER -> "Пользователь"
            UserRole.BUSINESS -> "Бизнес"
            UserRole.PREMIUM -> "Премиум"          // ✅ НОВОЕ
            UserRole.ADMIN -> "Администратор"
        }
    }
/*!
    // ✅ Переход на следующий тариф
    fun upgradeToNextPlan(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val uid = Firebase.auth.currentUser?.uid ?: run {
            onError("Пользователь не авторизован")
            return
        }
        val currentRole = userRole.value
        val nextRole = getNextRole(currentRole) ?: run {
            onError("У вас уже максимальный тариф")
            return
        }
        Firebase.firestore.collection("users")
            .document(uid)
            .update("role", nextRole.name)
            .addOnSuccessListener {
                userRole.value = nextRole
                //_userRole.value = nextRole
                Log.d("MyLog", "Тариф обновлён: ${currentRole.name} → ${nextRole.name}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("MyLog", "Ошибка обновления тарифа: ${e.message}")
                onError(e.message ?: "Неизвестная ошибка")
            }
    }*/

    // ✅ Выход с закрытием приложения
    fun exitApp() {
        logout()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    // ✅ Обработка клика по кнопке входа/выхода
    fun onAuthButtonClick() = viewModelScope.launch {
        if (isAuthorized.value) {
            // Пользователь авторизован — предлагаем выйти
            _authEvents.emit(AuthEvent.ShowLogOutDialog)
        } else {
            // Пользователь не авторизован — предлагаем войти
            _authEvents.emit(AuthEvent.ShowLogInDialog)
        }
    }

    fun showLogoutTariffDialog() {
        isLogoutDialog.value = true
        showAuthDialog.value = true
    }


/*!    // ✅ Анонимный вход
    fun loginAnonymously() = viewModelScope.launch {
        try {
            authDataSource.signInAnonymously()
            Log.d("MyLog", "Анонимный вход успешен")
            // authStateListener автоматически загрузит роль
        } catch (e: Exception) {
            Log.e("MyLog", "Ошибка анонимного входа: ${e.message}")
            sendUiState(MainUiState.Error("Ошибка входа: ${e.message}"))
        }
    }*/

  /*!  // ✅ Переход к регистрации
    fun navigateToRegistration() = viewModelScope.launch {
        _authEvents.emit(AuthEvent.NavigateToRegistration("Для доступа требуется регистрация"))
    }*/

    fun logout() {
        roleJob?.cancel()

        Firebase.auth.signOut()
        authDataSource.signOut()

        _userRole.value = UserRole.ANONYMOUS
        _isAuthorized.value = false
        _isAdminState.value = false

        isRegisterState.value = false
        preferenceDataSource.clearUserSession()
        refreshHeader()

        Log.d("MyLog", "logout isAdminState.value = false")
    }
    // ✅ Закрытие диалога «Ваш тариф»
    fun dismissAuthDialog() {
        showAuthDialog.value = false
    }
    fun refreshAccessFlags() {
        val uid = authStateProvider.currentUser()?.uid

        if (uid == null) {
            Log.d("ROLE_DEBUG", "refreshAccessFlags: uid=null → сбрасываем флаги")


            _isAdminState.value = false
            isRegisterState.value = false
            return
        }
        viewModelScope.launch {
            val isAdmin = userAccessRepository.isAdmin(uid) || _userRole.value == UserRole.ADMIN
            _isAdminState.value = isAdmin
            if (isAdmin) {
                _userRole.value = UserRole.ADMIN
                preferenceDataSource.saveRole(UserRole.ADMIN)
                refreshHeader()
            }
            isRegisterState.value = userAccessRepository.isRegistered(uid)
            Log.d(
                "ROLE_DEBUG",
                "refreshAccessFlags: isAdmin($uid)=$isAdmin, isRegistered($uid)=${isRegisterState.value}"
            )

        }
    }
 /*!   // 2. ИСПРАВЛЕНИЕ: Убираем !! чтобы избежать краша у неавторизованных пользователей
    fun isAdmin(onAdmin: (Boolean) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: run {
            onAdmin(false) // Если нет пользователя, он точно не админ
            return
        }

        Firebase.firestore.collection("admin")
            .document(uid)
            .get()
            .addOnSuccessListener {
                onAdmin(it.get("isAdmin") as? Boolean ?: false)
            }
            .addOnFailureListener {
                onAdmin(false)
            }
    }

    // 3. ИСПРАВЛЕНИЕ: Аналогично для проверки регистрации
    fun isUserRegistered(onRegister: (Boolean) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: run {
            onRegister(false)
            return
        }

        Firebase.firestore.collection("guide_users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                // Анонимный пользователь не пройдет эту проверку, вернется false
                onRegister(it.get("isRegistered") as? Boolean ?: false)
            }
            .addOnFailureListener {
                onRegister(false)
            }
    }*/
}
