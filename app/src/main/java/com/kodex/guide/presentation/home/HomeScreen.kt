package com.kodex.guide.presentation.home

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.presentation.home.CategoryCarousel
import com.kodex.guide.presentation.home.ShowTariffDialog
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.ui.dialods.FilterDialog
import com.kodex.guide.ui.theme.Orange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    //savedViewModel: SavedPostsViewModel = hiltViewModel(),
    viewModel: HomeViewModel = hiltViewModel(),
    navData: NavRoutes.HomeDataObject,
    onBookEditClick: (Book) -> Unit,
    onBookClick: (Book) -> Unit,
    book: Book = Book(),
    onAdminClick: () -> Unit,
    onAnonymousClick: () -> Unit,
    onLoginClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSavedClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onRegistrationNeeded: () -> Unit,
    onEnter: () -> Unit,
    showMyPosts: Boolean = true,
    onMyPostsClick: () -> Unit = {}

    ) {
    // Подписываемся на роль пользователя
    val userRole by viewModel.userRole.collectAsState()      // ✅ Flow
    val isAdmin by viewModel.isAdminState.collectAsState()   // ✅ Flow
    val showAuthDialog by viewModel.showAuthDialog
    val isLogoutDialog by viewModel.isLogoutDialog
    val headerUser by viewModel.headerUser
    val showPaymentSheet = viewModel.showPaymentSheet

    val savedViewModel: SavedPostsViewModel = hiltViewModel()
    val savedPosts by savedViewModel.savedPosts.collectAsStateWithLifecycle()
    val savedKeys by savedViewModel.savedKeys.collectAsStateWithLifecycle()
    val ratingsByBook by viewModel.ratingsByBook.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val categoryList = stringArrayResource(id = R.array.category_drawer)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val bookToDelete by viewModel.bookToDelete
    val isAuthorState = remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val books = viewModel.books.collectAsLazyPagingItems()
    var lastDisplayedBooks by remember { mutableStateOf(emptyList<Book>()) }
    var refreshStarted by remember { mutableStateOf(false) }
    var hasCompletedBooksRequest by remember { mutableStateOf(false) }
    var isTopBarCollapsed by remember { mutableStateOf(false) }
    val topBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val scrollDelta = consumed.y + available.y
                if (scrollDelta < -2f) {
                    isTopBarCollapsed = true
                } else if (scrollDelta > 2f) {
                    isTopBarCollapsed = false
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(books.itemSnapshotList, savedPosts, lastDisplayedBooks) {
        viewModel.loadRatingsForBooks(
            (books.itemSnapshotList.items + savedPosts + lastDisplayedBooks).distinctBy(Book::key)
        )
    }

    LaunchedEffect(
        savedPosts,
        viewModel.hasSelectedCategory.value,
        hasCompletedBooksRequest
    ) {
        if (!viewModel.hasSelectedCategory.value &&
            !hasCompletedBooksRequest &&
            lastDisplayedBooks.isEmpty()
        ) {
            lastDisplayedBooks = savedPosts
        }
    }

    LaunchedEffect(viewModel.categoryState.value) {
        lastDisplayedBooks = emptyList()
        hasCompletedBooksRequest = false
    }

    LaunchedEffect(books.loadState.refresh, books.itemSnapshotList) {
        if (books.loadState.refresh is LoadState.Loading) {
            refreshStarted = true
        } else if (books.loadState.refresh is LoadState.NotLoading) {
            val currentBooks = books.itemSnapshotList.items
            if (refreshStarted) {
                lastDisplayedBooks = currentBooks
                hasCompletedBooksRequest = true
                refreshStarted = false
            } else if (currentBooks.isNotEmpty()) {
                lastDisplayedBooks = currentBooks
            }
        }
    }

    val state = rememberPullToRefreshState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val lifecycleOwner = LocalLifecycleOwner.current

    // ✅ Подписка на события авторизации
    LaunchedEffect(Unit) {
        viewModel.authEvents.collect { event ->
            when (event) {
                is AuthEvent.ShowLogInDialog -> {
                    viewModel.showAuthDialog.value = true
                    viewModel.isLogoutDialog.value = false
                }

                is AuthEvent.ShowLogOutDialog -> {
                    viewModel.showAuthDialog.value = true
                    viewModel.isLogoutDialog.value = true
                }

                is AuthEvent.NavigateToRegistration -> {
                    onRegistrationNeeded()
                }

                is AuthEvent.NavigateToSignIn -> {  // ✅ НОВОЕ
                    onLoginClick()                  // SignIn
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        savedViewModel.events.collect { event ->
            when (event) {
                is SavedUiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiState.collect { uiState ->
            if (uiState is HomeViewModel.MainUiState.Error) {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(books.loadState.refresh) {
        if (books.loadState.refresh is LoadState.Error) {
            val errorMessage = (books.loadState.refresh as LoadState.Error).error.message
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.fillMaxWidth(),
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (!isLandscape) 0.77f else 0.3f)
                    .clip(RoundedCornerShape(bottomEnd = 24.dp))
                    .background(Color.White)
            ) {
                if (!isLandscape) {
                    DrawerHeader(user = headerUser)   // ✅ передаём весь User?

                }
                DrawerBody(
                    showMyPosts = showMyPosts,
                    onAdminClick = onAdminClick,
                    onAddBookClick = onAddClick,
                    onCategoryClick = { category ->
                        viewModel.onCategorySelected(category) // Вся логика теперь в ViewModel
                        coroutineScope.launch { drawerState.close() }
                    },
                    onLoginClick = {
                        onLoginClick()
                        coroutineScope.launch { drawerState.close() }
                    },

                    onAnonymousClick = {
                        onAnonymousClick()
                        coroutineScope.launch { drawerState.close() }
                    },

                    onSettingsClick = {
                        onSettingsClick()
                        coroutineScope.launch { drawerState.close() }
                    },

                    onRegistrationNeeded = {
                        onRegistrationNeeded()
                        coroutineScope.launch { drawerState.close() }
                    },
                    onEnter = {
                        onEnter()
                        coroutineScope.launch { drawerState.close() }
                    },
                    onMyPostsClick = {
                        onMyPostsClick()
                        coroutineScope.launch { drawerState.close() }
                    },

                    )
            }
        }
    ) {
        Scaffold(
            // FAB добавляется здесь
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAddClick() },
                    containerColor = Orange.copy(alpha = 0.6F),
                    contentColor = Color.White,

                    ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить объявление"
                    )
                }
            },
            topBar = {
                if (!isLandscape)
                    MainTopBar(
                        category = viewModel.categoryState.value,
                        isListLayout = viewModel.showTabOneOrTo.value,
                        isCollapsed = isTopBarCollapsed,
                        isSavedSelected = viewModel.selectedBottomItemState.intValue == BottomMenuItem.Saved.titleId,
                        onSearch = { searchText ->
                            viewModel.searchBook(searchText)
                        },
                        onFilter = {
                            showFilterDialog = true
                        },
                        onTab = {
                            viewModel.showTabOneOrTo.value = !viewModel.showTabOneOrTo.value
                        },
                        onMenu = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topBarScrollConnection),
            bottomBar = {
                if (!isLandscape)
                    BottomMenu(
                        viewModel.selectedBottomItemState.intValue,
                        onCategoryClick = {
                            viewModel.getAllBooksFromCategory(category = BookCategories.ALL)
                            viewModel.selectedBottomItemState.intValue =
                                BottomMenuItem.Saved.titleId


                        },
                        onHomeClick = {
                            // получаем список с иыентификатором и
                            viewModel.selectedBottomItemState.intValue = BottomMenuItem.Home.titleId
                            viewModel.getAllBooksFromCategory(category = BookCategories.ALL)
                        },
                        onSettingsClick = {
                            onSettingsClick()
                            viewModel.selectedBottomItemState.intValue =
                                BottomMenuItem.Settings.titleId

                        },
                        onSavedClick = {
                            viewModel.selectedBottomItemState.intValue = BottomMenuItem.Saved.titleId
                            onSavedClick()
                        }
                    )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CategoryCarousel(
                    selectedCategory = viewModel.categoryState.value,
                    onCategoryClick = { category ->
                        viewModel.onCategorySelected(category)
                    }
                )
                if (bookToDelete != null) {
                    DeleteBookDialog(
                        onDismiss = { viewModel.onDeleteDialogDismissed() },
                        onConfirm = { viewModel.deleteBook() }
                    )
                }

                PullToRefreshBox(
                    isRefreshing = books.loadState.refresh is LoadState.Loading,
                    onRefresh = {
                        viewModel.refreshBooks()
                    },
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        Indicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp),
                            isRefreshing = books.loadState.refresh is LoadState.Loading,
                            containerColor = Color.LightGray,
                            color = Orange,
                            state = state

                        )
                    }
                ) {

                    // Проверяем, какая вкладка сейчас выбрана и каково состояние загрузки
                    val isSavedTabSelected = viewModel.selectedBottomItemState.intValue == BottomMenuItem.Saved.titleId
                    val isRefreshing = books.loadState.refresh is LoadState.Loading
                    val isBooksEmpty = books.itemCount == 0

                    if (isSavedTabSelected) {
                        // Вкладка "Сохраненные" - всегда показываем список из Room
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        ) {
                            if (savedPosts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Нет сохраненных объявлений")
                                    }
                                }
                            } else {
                                items(savedPosts, key = { it.key }) { book ->
                                    BookListItemUi(
                                        heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                        titleIndex = viewModel.categoryState.value.id,
                                        showEditButton = viewModel.isAdminState.collectAsState().value,
                                        book = book,
                                        ratings = ratingsByBook[book.key] ?: book.ratingsList,
                                        isSaved = true, // На этой вкладке все книги гарантированно сохранены
                                        onBookClick = { bk ->
                                            onBookClick(bk)
                                        },
                                        onEditClick = {
                                            onBookEditClick(it)
                                        },
                                        onDeleteClick = { bookToDelete ->
                                            viewModel.onDeleteRequested(bookToDelete)
                                        },
                                        onSavedRoomClick = {
                                            savedViewModel.toggle(book)
                                        }
                                    )
                                    Spacer(Modifier.padding(5.dp))
                                }
                            }
                        }
                    } else {
                        // Вкладка "Главная" (или другие)
                        if (isRefreshing || isBooksEmpty) {
                            if (lastDisplayedBooks.isNotEmpty()) {
                                LazyVerticalStaggeredGrid(
                                    columns = StaggeredGridCells.Fixed(
                                        if (viewModel.showTabOneOrTo.value) 1 else 2
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(count = lastDisplayedBooks.size) { index ->
                                        val book = lastDisplayedBooks[index]
                                        val isSaved = savedKeys.contains(book.key)
                                        BookListItemUi(
                                            heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                            titleIndex = viewModel.categoryState.value.id,
                                            showEditButton = viewModel.isAdminState.collectAsState().value,
                                            book = book,
                                            ratings = ratingsByBook[book.key] ?: book.ratingsList,
                                            isSaved = isSaved,
                                            onBookClick = { onBookClick(it) },
                                            onEditClick = { onBookEditClick(it) },
                                            onDeleteClick = { viewModel.onDeleteRequested(it) },
                                            onSavedRoomClick = { savedViewModel.toggle(book) }
                                        )
                                    }
                                }
                            } else if (!viewModel.hasSelectedCategory.value &&
                                !hasCompletedBooksRequest
                            ) {
                                LazyColumn(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(2.dp)
                                ) {
                                    items(savedPosts, key = { it.key }) { book ->
                                        val isSaved = savedKeys.contains(book.key)
                                        BookListItemUi(
                                            heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                            titleIndex = viewModel.categoryState.value.id,
                                            showEditButton = viewModel.isAdminState.collectAsState().value,
                                            book = book,
                                            ratings = ratingsByBook[book.key] ?: book.ratingsList,
                                            isSaved = isSaved,
                                            onBookClick = { bk ->
                                                onBookClick(bk)
                                            },
                                            onEditClick = {
                                                onBookEditClick(it)
                                            },
                                            onDeleteClick = { bookToDelete ->
                                                viewModel.onDeleteRequested(bookToDelete)
                                            },
                                            onSavedRoomClick = {
                                                savedViewModel.toggle(book)
                                            }
                                        )
                                        Spacer(Modifier.padding(5.dp))
                                    }
                                }
                            }
                        } else {
                            // Основной список загрузился
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(if (viewModel.showTabOneOrTo.value == true) 1 else 2),
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(count = books.itemCount) { index ->
                                    val book = books[index]
                                    if (book != null) {
                                        val isSaved = savedKeys.contains(book.key)
                                        BookListItemUi(
                                            heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                            titleIndex = viewModel.categoryState.value.id,
                                            showEditButton = viewModel.isAdminState.collectAsState().value,
                                            book = book,
                                            ratings = ratingsByBook[book.key] ?: book.ratingsList,
                                            isSaved = isSaved,
                                            onBookClick = { bk ->
                                                onBookClick(bk)
                                            },
                                            onEditClick = {
                                                onBookEditClick(it)
                                            },
                                            onDeleteClick = { bookToDelete ->
                                                viewModel.onDeleteRequested(bookToDelete)
                                            },
                                            onSavedRoomClick = {
                                                savedViewModel.toggle(book)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    /*// В начале загрузка из базы данных Room
                    // Проверяем, какая вкладка сейчас выбрана в нижнем меню
                    val isSavedTabSelected =
                        viewModel.selectedBottomItemState.intValue == BottomMenuItem.Saved.titleId

                    if (isSavedTabSelected) {
                        // Отображаем список сохраненных из Room
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        ) {
                            if (savedPosts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Нет сохраненных объявлений")
                                    }
                                }
                            } else {
                                items(savedPosts, key = { it.key }) { book ->
                                    BookListItemUi(
                                        heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                        titleIndex = viewModel.categoryState.value.id,
                                        showEditButton = viewModel.isAdminState.collectAsState().value,
                                        book = book,
                                        isSaved = true, // На этой вкладке все книги гарантированно сохранены
                                        onBookClick = { bk ->
                                            onBookClick(bk)
                                        },
                                        onEditClick = {
                                            onBookEditClick(it)
                                        },
                                        onDeleteClick = { bookToDelete ->
                                            viewModel.onDeleteRequested(bookToDelete)
                                        },
                                        onSavedRoomClick = {
                                            savedViewModel.toggle(book)
                                        }
                                    )
                                    Spacer(Modifier.padding(5.dp))
                                }
                            }
                        }
                    } else {
                        // Отображаем основной список книг (PagingData)
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(if (viewModel.showTabOneOrTo.value == true) 1 else 2),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(count = books.itemCount) { index ->
                                val book = books[index]
                                if (book != null) {
                                    val isSaved = savedKeys.contains(book.key)
                                    BookListItemUi(
                                        heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                        titleIndex = viewModel.categoryState.value.id,
                                        showEditButton = viewModel.isAdminState.collectAsState().value,
                                        book = book,
                                        isSaved = isSaved,
                                        onBookClick = { bk ->
                                            onBookClick(bk)
                                        },
                                        onEditClick = {
                                            onBookEditClick(it)
                                        },
                                        onDeleteClick = { bookToDelete ->
                                            viewModel.onDeleteRequested(bookToDelete)
                                        },
                                        onSavedRoomClick = {
                                            savedViewModel.toggle(book)
                                        }
                                    )
                                }
                            }
                        }
                    }*/
                    /* if (books.itemCount == 0)
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        ) {
                            items(savedPosts) { book ->
                                    val isSaved = savedKeys.contains(book.key)
                                    BookListItemUi(
                                        heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                        titleIndex = viewModel.categoryState.value.id,
                                        showEditButton = viewModel.isAdminState.collectAsState().value,
                                        book = book,
                                        isSaved = isSaved,
                                        onBookClick = { bk ->
                                            onBookClick(bk)
                                        },
                                        onEditClick = {
                                            onBookEditClick(it)
                                        },
                                        onDeleteClick = { bookToDelete ->
                                            viewModel.onDeleteRequested(bookToDelete)
                                        },
                                        onSavedRoomClick = {
                                            savedViewModel.toggle(book)
                                        }
                                    )
                                    Spacer(Modifier.padding(5.dp))
                            }
                        } else {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(if (viewModel.showTabOneOrTo.value == true) 1 else 2),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(count = books.itemCount) { index ->
                                val book = books[index]
                                if (book != null) {
                                    val isSaved = savedKeys.contains(book.key)

                                    BookListItemUi(
                                        heightValue = if (viewModel.showTabOneOrTo.value) 1 else 2,
                                        titleIndex = viewModel.categoryState.value.id,
                                        showEditButton = viewModel.isAdminState.collectAsState().value,
                                        book = book,
                                        isSaved = isSaved,
                                        onBookClick = { bk ->
                                            onBookClick(bk)
                                        },
                                        onEditClick = {
                                            onBookEditClick(it)
                                        },
                                        onDeleteClick = { bookToDelete ->
                                            viewModel.onDeleteRequested(bookToDelete)
                                        },
                                        onSavedRoomClick = {
                                            savedViewModel.toggle(book)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
*/
                    FilterDialog(
                        showDialog = showFilterDialog,
                        onConfirm = {
                            showFilterDialog = false
                        },
                        onDismiss = {
                            showFilterDialog = false
                        }
                    )
                    ShowTariffDialog(viewModel, userRole)

                    // Обёртка для отображения bottom sheet
                    if (showPaymentSheet.value) {
                        PaymentBottomSheet(
                            viewModel = viewModel,
                            onDismiss = { showPaymentSheet.value = false },
                            onPaymentSuccess = {
                                // Можно показать Toast или обновить UI
                                Toast.makeText(context, "Роль обновлена!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                }
            }
        }
    }
}
