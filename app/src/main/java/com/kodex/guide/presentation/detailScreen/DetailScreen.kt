package com.kodex.guide.presentation.detailScreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kodex.guide.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.data.service.WorkingHoursServiceImpl
import com.kodex.guide.domain.service.WorkingHoursService
import com.kodex.guide.presentation.components.ActionSheetButton
import com.kodex.guide.presentation.events.DetailUiEvents
import com.kodex.guide.presentation.home.BookListItemUi
import com.kodex.guide.presentation.home.SavedPostsViewModel
import com.kodex.guide.ui.theme.IconBgLight
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.TextDark
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    key: String,
    viewModel: DetailsScreenViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onCommentClick: () -> Unit,
    bookCategory: BookCategories = BookCategories.OTHER,
    onRelatedBookClick: (Book) -> Unit = {},
    timeService: WorkingHoursService = WorkingHoursServiceImpl(),
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val collapsedHeight = 280.dp
    val expandedHeight = screenHeight * 0.5f

    var showFullScreenImage by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageHeight = remember { Animatable(expandedHeight.value) }
    val gridState = rememberLazyStaggeredGridState()
    var showActionsSheet by remember { mutableStateOf(false) }

    // 1. Получаем данные из ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val book = uiState.book // <-- Может быть null, пока идет загрузка

    val savedViewModel: SavedPostsViewModel = hiltViewModel()
    val savedKeys by savedViewModel.savedKeys.collectAsStateWithLifecycle()
    val relatedBooks by viewModel.relatedBooks.collectAsStateWithLifecycle()
    val ratingsList = uiState.comments.mapNotNull { it.rating }

    // 2. СНАЧАЛА ПРОВЕРКА: Если книга еще грузится, показываем лоадер и ВЫХОДИМ из функции
    if (book == null) {
        Log.d("MyLog", "Book is null")
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Orange)
        }

        return // <-- ВАЖНО: Код ниже не выполнится, пока book == null
    }

    // 3. ТЕПЕРЬ book ГАРАНТИРОВАННО НЕ NULL (Компилятор это понимает)

    // Запускаем загрузку сопутствующих данных (похожие посты, комменты)
    LaunchedEffect(book.key) {
        viewModel.loadRelatedBooks(bookCategory, book.key)
        viewModel.onEvent(DetailUiEvents.DetailUiEvent.GetCommentsEvent(book.key))
    }

    // Логика работы часов
    var isOpenNow by remember(book.openingHours) {
        mutableStateOf(timeService.isOpenNow(book.openingHours))
    }
    LaunchedEffect(book.openingHours) {
        while (true) {
            isOpenNow = timeService.isOpenNow(book.openingHours)
            delay(60_000)
        }
    }

    // Декодирование картинки
    val bitmap by remember(book.imageUrl) {
        mutableStateOf<Bitmap?>(runCatching {
            val base64Image = Base64.decode(book.imageUrl, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(base64Image, 0, base64Image.size)
        }.getOrNull())
    }

    // Отслеживание скролла для BottomSheet
    val sheetVisible by remember(book) {
        derivedStateOf {
            val info = gridState.layoutInfo
            val mainItem = info.visibleItemsInfo.firstOrNull { it.index == 0 }
            mainItem != null && (mainItem.offset.y + mainItem.size.height) <= info.viewportEndOffset
        }
    }
    LaunchedEffect(sheetVisible) {
        showActionsSheet = sheetVisible
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newHeight = (imageHeight.value - delta).coerceIn(
                    collapsedHeight.value,
                    expandedHeight.value
                )
                coroutineScope.launch {
                    imageHeight.animateTo(newHeight, animationSpec = tween(durationMillis = 0))
                }
                return Offset.Zero
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // ОСНОВНОЙ ПОСТ
                item(span = StaggeredGridItemSpan.FullLine) {
                    MainContent(
                        book = book, // Теперь здесь точно валидный объект
                        isOpenNow = isOpenNow,
                        comments = uiState.comments,
                        ratingData = uiState.ratingData,
                        showRateDialog = uiState.showRateDialog,
                        showCommentDialog = uiState.showCommentDialog,
                        ratingDataToShow = uiState.ratingDataToShow,
                        onCommentClick = { onCommentClick() },
                        onNavigateToReviews = onNavigateToReviews,
                        onShowMapClick = { showMap = true },
                        onShowImageClick = { showFullScreenImage = true },
                        onBackPressed = { onBackPressed() },
                        viewModel = viewModel
                    )
                }
                // ПОХОЖИЕ ПОСТЫ
                items(relatedBooks) { relatedBook ->
                    BookListItemUi(
                        heightValue = 2,
                        titleIndex = relatedBook.categoryIndex.id,
                        showEditButton = false,
                        book = relatedBook,
                        isSaved = savedKeys.contains(relatedBook.key),
                        onBookClick = { onRelatedBookClick(it) },
                        onSavedRoomClick = { savedViewModel.toggle(relatedBook) }
                    )
                }
            }
        }
    }

    // BottomSheet (Убрали лишние if, так как book не null)
    if (showActionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IconBgLight,
                        modifier = Modifier.clickable { onNavigateToReviews() }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (ratingsList.isNotEmpty()) {
                                    String.format("%.1f", ratingsList.average())
                                } else "0.0",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Star, null, tint = Orange, modifier = Modifier.size(25.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(text = "(${ratingsList.size})", fontSize = 14.sp, color = Color.Gray)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, modifier = Modifier.size(22.dp), tint = Color(0xFF3B82F6))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = book.openingHours,
                                fontSize = 15.sp,
                                color = TextDark,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (!isOpenNow) "Сейчас закрыто" else "Сейчас открыто",
                                fontSize = 13.sp,
                                color = if (!isOpenNow) Orange else Color(0xFF10B981),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionSheetButton(
                        text = "Оценить",
                        icon = Icons.Default.Star,
                        containerColor = Orange,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showActionsSheet = false
                            viewModel.onEvent(
                                DetailUiEvents.DetailUiEvent.ShowUserRatingDialogEvent(book.key)
                            )
                        }
                    )

                    ActionSheetButton(
                        text = stringResource(id = R.string.call),
                        icon = Icons.Default.Call,
                        containerColor = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val phone = book.telephone.filter { it.isDigit() || it == '+' }
                            if (phone.isNotEmpty()) {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$phone")
                                    })
                                }
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    // Полноэкранное фото
    if (showFullScreenImage) {
        Dialog(
            onDismissRequest = { showFullScreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black).clickable { showFullScreenImage = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = "Увеличенное фото",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showFullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(40.dp)
                        .size(44.dp)
                        .background(Color(0xFF2196F3).copy(alpha = 0.55f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, "Закрыть", tint = Color.White)
                }
            }
        }
    }

    // Карта
    val destinationLat = book.latitude?.toDouble()
    val destinationLng = book.longitude?.toDouble()
    if (showMap && book.location &&
        destinationLat != null && destinationLat.isFinite() && destinationLat in -90.0..90.0 &&
        destinationLng != null && destinationLng.isFinite() && destinationLng in -180.0..180.0
    ) {
        MapScreen(
            destinationLat = destinationLat,
            destinationLng = destinationLng,
            destinationTitle = book.title,
            onDismiss = { showMap = false }
        )
    }
}