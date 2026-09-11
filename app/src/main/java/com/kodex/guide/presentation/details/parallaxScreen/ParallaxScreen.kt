package com.kodex.guide.presentation.details.parallaxScreen


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.presentation.detailScreen.DetailsScreenViewModel
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.presentation.components.ActionSheetButton
import com.kodex.guide.presentation.events.DetailUiEvents
import com.kodex.guide.presentation.home.BookListItemUi
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.presentation.home.SavedPostsViewModel
import com.kodex.guide.presentation.login.LoginButton
import com.kodex.guide.ui.theme.ButtonColor
import com.kodex.guide.ui.theme.IconBgLight
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.TextDark
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParallaxScreen(
    viewModel: DetailsScreenViewModel = hiltViewModel(),
    navObject: NavRoutes.ParallaxNavObject = NavRoutes.ParallaxNavObject(),
    onBackPressed: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onCommentClick: () -> Unit,
    bookCategory: BookCategories = BookCategories.ALL,
    onRelatedBookClick: (Book) -> Unit = {},

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
    var bitmap: Bitmap? = null

    try {
        val base64Image = Base64.decode(navObject.imageUrl, Base64.DEFAULT)
        bitmap = BitmapFactory.decodeByteArray(base64Image, 0, base64Image.size)
    } catch (e: IllegalArgumentException) {
    }

    val listState = rememberLazyListState()



    val savedViewModel: SavedPostsViewModel = hiltViewModel()
    val savedKeys by savedViewModel.savedKeys.collectAsStateWithLifecycle()
    val relatedBooks by viewModel.relatedBooks.collectAsStateWithLifecycle()

    val gridState = rememberLazyStaggeredGridState()
    var showActionsSheet by remember { mutableStateOf(false) }

// ✅ конец основного поста (индекс 0) дошел до низа экрана
    val sheetVisible by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            val mainItem = info.visibleItemsInfo.firstOrNull { it.index == 0 }
            mainItem != null &&
                    (mainItem.offset.y + mainItem.size.height) <= info.viewportEndOffset
        }
    }

// ✅ всплыл в конце основного поста; скрылся, когда скролл пошел дальше
    LaunchedEffect(sheetVisible) {
        showActionsSheet = sheetVisible
    }
    LaunchedEffect(navObject.bookId) {
        viewModel.loadRelatedBooks(bookCategory, navObject.bookId)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(
            DetailUiEvents.DetailUiEvent.GetCommentsEvent(navObject.bookId)
        )
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

    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),   // ✅ по 2 в строке, как в HomeScreen
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                // ✅ ОСНОВНОЙ ПОСТ — НА ВСЮ ШИРИНУ
                item(span = StaggeredGridItemSpan.FullLine) {
                    MainContent(
                        navObject = navObject,
                        onCommentClick = { onCommentClick() },
                        onNavigateToReviews = onNavigateToReviews,
                        onShowMapClick = { showMap = true },
                        onBackPressed = { onBackPressed() }
                    )
                }
                // ✅ ПОСТЫ ЭТОЙ ЖЕ КАТЕГОРИИ

                    items(relatedBooks) { book ->
                        BookListItemUi(
                            heightValue = 2,
                            titleIndex = book.categoryIndex.id,
                            showEditButton = false,
                            book = book,
                            isSaved = savedKeys.contains(book.key),          // ✅ реальный статус сохранения
                            onBookClick = { onRelatedBookClick(it) },        // ✅ открытие поста
                            onSavedRoomClick = { savedViewModel.toggle(book) } // ✅ закладка
                        )
                    }
            }
        }
    }
    if (showActionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // ✅ РЕЙТИНГ СЛЕВА — ВРЕМЯ РАБОТЫ СПРАВА, прижаты к краям
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // слева: рейтинг
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IconBgLight,
                        modifier = Modifier.clickable { onNavigateToReviews() }
                    ) {
                        // слева: рейтинг + количество отзывов
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (navObject.ratingsList.isNotEmpty()) {
                                    String.format("%.1f", navObject.ratingsList.average())
                                } else {
                                    "0.0"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Orange,
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            // ✅ КОЛИЧЕСТВО ОТЗЫВОВ
                            Text(
                                text = "(${navObject.ratingsList.size})",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // справа: время работы
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFF3B82F6)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = navObject.openingHours,
                                fontSize = 15.sp,
                                color = TextDark,
                                fontWeight = FontWeight.Medium
                            )
                            if (!navObject.isOpenNow) {
                                Text(
                                    text = "Сейчас закрыто",
                                    fontSize = 13.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Сейчас открыто",
                                    fontSize = 13.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ✅ КНОПКИ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ОЦЕНИТЬ — оранжевая слева
                    ActionSheetButton(
                        text = "Оценить",
                        icon = Icons.Default.Star,
                        containerColor = Orange,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showActionsSheet = false
                            viewModel.onEvent(
                                DetailUiEvents.DetailUiEvent.ShowUserRatingDialogEvent(navObject.bookId)
                            )
                        }
                    )

                    // ПОЗВОНИТЬ — синяя справа
                    ActionSheetButton(
                        text = stringResource(id = R.string.call),
                        icon = Icons.Default.Call,
                        containerColor = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // ✅ номер телефона в вашей модели лежит в price
                            val phone = navObject.telephone.toString()
                                .filter { it.isDigit() || it == '+' }
                            if (phone.isNotEmpty()) {
                                runCatching {
                                    context.startActivity(
                                        Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:$phone")
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
    if (showFullScreenImage) {
        Dialog(
            onDismissRequest = { showFullScreenImage = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showFullScreenImage = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = "Увеличенное фото",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showFullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(40.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
                }
            }
        }
    }

    if (showMap) {
        MapScreen(
            destinationLat = navObject.latitude.toDoubleOrNull() ?: 55.7558,
            destinationLng = navObject.longitude.toDoubleOrNull() ?: 37.6173,
            destinationTitle = navObject.title,
            onDismiss = { showMap = false }
        )
    }
}
