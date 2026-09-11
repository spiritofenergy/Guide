package com.kodex.guide.presentation.home

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kodex.guide.domain.model.Book
import com.kodex.guide.ui.dialods.FilterDialog
import com.kodex.bookmarketcompose.R
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.UserRole
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
    onCategoryClick: () -> Unit,
    onRegistrationNeeded: () -> Unit,
    onEnter: () -> Unit,
    showMyPosts: Boolean = false,       // ✅ НОВОЕ
    onMyPostsClick: () -> Unit = {}     // ✅ НОВОЕ

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

    val context = LocalContext.current
    val categoryList = stringArrayResource(id = R.array.category_array)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val bookToDelete by viewModel.bookToDelete
    val isAuthorState = remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val books = viewModel.books.collectAsLazyPagingItems()

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
            Column(modifier = Modifier.fillMaxWidth(if (!isLandscape) 0.7f else 0.3f)) {
                if (!isLandscape) {
                    DrawerHeader(user = headerUser)   // ✅ передаём весь User?

                }
                DrawerBody(
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
                        viewModel.categoryState.value,
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
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!isLandscape)
                    BottomMenu(
                        viewModel.selectedBottomItemState.intValue,
                        onCategoryClick = {
                            viewModel.getAllBooksFromCategory(category = BookCategories.OTHER)
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

                        }
                    )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (bookToDelete != null) {
                    DeleteBookDialog(
                        onDismiss = { viewModel.onDeleteDialogDismissed() },
                        onConfirm = { viewModel.deleteBook() }
                    )
                }

                PullToRefreshBox(
                    isRefreshing = books.loadState.refresh is LoadState.Loading,
                    onRefresh = {
                        //  refreshBooks(books, viewModel)
                    },
                    state = state,
                    modifier = Modifier.padding(),
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = books.loadState.refresh is LoadState.Loading,
                            containerColor = Color.LightGray,
                            color = Orange,
                            state = state

                        )
                    }
                ) {
                    // В начале загрузка из базы данных Room
                    if (books.itemCount == 0)
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

            FilterDialog(
                showDialog = showFilterDialog,
                onConfirm = {
                    showFilterDialog = false
                },
                onDismiss = {
                    showFilterDialog = false
                }
            )
            if (showAuthDialog) {
                val currentRole = userRole
                val nextRole = viewModel.getNextRole(currentRole)

                TariffDialog(
                    currentRoleName = viewModel.getRoleDisplayName(currentRole),
                    nextRoleName = nextRole?.let { viewModel.getRoleDisplayName(it) },
                    nextRequiresPayment = nextRole?.let { viewModel.requiresPayment(it) } ?: false,
                    showNextButton = nextRole != null,
                    showPremiumButton = nextRole != null && nextRole != UserRole.PREMIUM,
                    showGuestButton = currentRole == UserRole.USER ||
                            currentRole == UserRole.BUSINESS ||
                            currentRole == UserRole.PREMIUM,
                    onDismiss = { viewModel.dismissAuthDialog() },
                    onNextTariffClick = {
                        viewModel.dismissAuthDialog()
                        viewModel.requestUpgrade(currentRole)
                    },
                    onPremiumClick = {
                        viewModel.dismissAuthDialog()
                        viewModel.requestPremiumUpgrade()
                    },
                    onLogoutClick = {
                        viewModel.dismissAuthDialog()
                        viewModel.logout()
                    }
                )
            }

            // Обёртка для отображения bottom sheet
            if (showPaymentSheet.value) {
                PaymentBottomSheet(
                    viewModel = viewModel,
                    onDismiss = { showPaymentSheet.value = false },
                    onPaymentSuccess = {
                        // Можно показать Toast или обновить UI
                        Toast.makeText(context, "Роль обновлена!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


