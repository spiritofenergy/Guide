package com.kodex.guide.presentation.details.parallaxScreen


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Streetview
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kodex.bookmarketcompose.R
import com.kodex.guide.presentation.components.ActionButton
import com.kodex.guide.presentation.components.ArrowCircleButton
import com.kodex.guide.presentation.components.DeliveryChip
import com.kodex.guide.presentation.components.InfoRow
import com.kodex.guide.presentation.components.SectionHeader
import com.kodex.guide.presentation.components.ServiceInfoRow
import com.kodex.guide.presentation.components.StatusChip
import com.kodex.guide.presentation.detailScreen.CommentListItem
import com.kodex.guide.presentation.detailScreen.DetailsScreenViewModel
import com.kodex.guide.presentation.events.DetailUiEvents
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.ui.dialods.DialogComments
import com.kodex.guide.ui.dialods.DialogRating
import com.kodex.guide.ui.theme.BackgroundWhite
import com.kodex.guide.ui.theme.IconBgLight
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.TextDark
import com.kodex.guide.ui.theme.TextGray
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainContent(
    onCommentClick: (NavRoutes.CommentsNavData) -> Unit = {},
    navObject: NavRoutes.ParallaxNavObject,
    onNavigateToReviews: () -> Unit,
    onShowMapClick: () -> Unit,
    onShowImageClick: () -> Unit,
    viewModel: DetailsScreenViewModel = viewModel(),
    onBackPressed: () -> Unit,
    isOpenNow: Boolean,
) {
    var showRateDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val telephone = navObject.telephone
    val scope = rememberCoroutineScope()

    val bitmap by remember(navObject.imageUrl) {
        mutableStateOf<Bitmap?>(runCatching {
            val base64Image = Base64.decode(navObject.imageUrl, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(base64Image, 0, base64Image.size)
        }.getOrNull())
    }
    DialogRating(
        ratingData = uiState.value.ratingData,
        onDismiss = { viewModel.onEvent(DetailUiEvents.DetailUiEvent.HideUserRatingDialog) },
        onSubmit = { ratingData ->
            viewModel.onEvent(
                DetailUiEvents.DetailUiEvent.InsertRatingDialogEvent(ratingData, navObject.bookId)
            )
        },
        show = uiState.value.showRateDialog,
    )

    DialogComments(
        showDialog = uiState.value.showCommentDialog,
        onDismiss = { showCommentDialog = false },
        ratingData = uiState.value.ratingDataToShow,
        onConfirm = { showCommentDialog = false }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ===== ВЕРХНЯЯ ЧАСТЬ: Фото как фон =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {

            // Фото на весь фон
            AsyncImage(
                model = bitmap,
                contentDescription = navObject.title,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (bitmap != null) Modifier.clickable(onClick = onShowImageClick)
                        else Modifier
                    ),
                contentScale = ContentScale.Crop,
               // transition = androidx.compose.animation.core.tween(durationMillis = 300)
            )

            // Затемнение снизу для читаемости текста
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 150f
                        )
                    )
            )
            // ===== ВЕРХНЯЯ ПАНЕЛЬ: Стрелка назад + Категория =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка назад (БЕЛАЯ)
                IconButton(
                    onClick = onBackPressed, // <-- НАВИГАЦИЯ НАЗАД
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White, // <-- БЕЛЫЙ ЦВЕТ
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                // Категория по центру
                Text(
                    text = stringArrayResource(id = R.array.category_drawer)[navObject.categoryIndex.id],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Контент поверх фото (внизу) - ВСЁ внутри одного Column
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {

                // Кнопки действий - ТОЖЕ внутри Column
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        icon = Icons.Default.Email,
                        label = "Email",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:ooozeeya@yandex.ru")
                            }
                            context.startActivity(intent)
                        }
                    )
                    ActionButton(
                        icon = Icons.Default.Call,
                        label = "Позвонить",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$telephone")
                            }
                            context.startActivity(intent)
                        }
                    )
                    ActionButton(
                        icon = Icons.Default.Share,
                        label = "Поделиться",
                        onClick = {
                            scope.launch {
                                viewModel.sharePlace(
                                    context = context,
                                    place = navObject,
                                    coroutineScope = scope
                                )
                            }
                        }
                    )
                    ActionButton(
                        icon = Icons.Default.Star,
                        label = "Оценка",
                        onClick = {
                            viewModel.onEvent(
                                DetailUiEvents.DetailUiEvent.ShowUserRatingDialogEvent(navObject.bookId)
                            )
                            showRateDialog = true
                        }
                    )
                }
            }
        }
        // ===== НИЖНЯЯ ЧАСТЬ: Белый фон =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Рейтинг и статус
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IconBgLight,
                    modifier = Modifier.clickable { onNavigateToReviews() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Orange
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val averageRating = if (navObject.ratingsList.isNotEmpty()) {
                            navObject.ratingsList.average()
                        } else 0.0
                        Text(
                            text = String.format("%.1f", averageRating),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        Text(
                            text = " (${navObject.ratingsList.size})",
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                }

                StatusChip(isOpen = isOpenNow)


                    if (navObject.delivery)
                        DeliveryChip()
                Spacer(modifier = Modifier.width(4.dp))


            }
// Название места + Цена в одну линию
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Название места (занимает основное пространство)
                Text(
                    text = navObject.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))

                // Цена справа
                Text(
                    text = "${navObject.price} р",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = navObject.description,

                fontSize = 15.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))


// ===== Секция: Адрес =====
            SectionHeader(title = "Адрес")

// Местоположение в одну линию с круглой кнопкой-стрелкой
            val latitude = navObject.latitude.toDoubleOrNull()
            val longitude = navObject.longitude.toDoubleOrNull()
            val hasMapCoordinates = navObject.location &&
                latitude != null && latitude.isFinite() && latitude in -90.0..90.0 &&
                longitude != null && longitude.isFinite() && longitude in -180.0..180.0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable(enabled = hasMapCoordinates) { onShowMapClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Левая часть: иконка + текст
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = IconBgLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF3B82F6)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Местоположение",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                        Text(
                            text = navObject.village,
                            fontSize = 15.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (hasMapCoordinates) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Посмотреть на",
                                fontSize = 12.sp,
                                color = TextGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " карте",
                                fontSize = 15.sp,
                                color = TextGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (hasMapCoordinates) {
                    Spacer(modifier = Modifier.width(8.dp))

                    ArrowCircleButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = onShowMapClick
                    )
                }
            }

// Детальный адрес в одну линию (улица, дом, квартира)
            if (navObject.street.isNotEmpty() || navObject.house.isNotEmpty() || navObject.flat.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Улица
                    if (navObject.street.isNotEmpty()) {
                        InfoRow(
                            icon = Icons.Default.Streetview,
                            iconTint = Color(0xFF3B82F6),
                            label = "Улица",
                            value = navObject.street,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (navObject.house.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        // Дом
                        InfoRow(
                            icon = Icons.Default.Home,
                            iconTint = Color(0xFF3B82F6),
                            label = "Дом",
                            value = navObject.house,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (navObject.flat.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        // Квартира
                        InfoRow(
                            icon = Icons.Default.MeetingRoom,
                            iconTint = Color(0xFF3B82F6),
                            label = "Кв.",
                            value = navObject.flat,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(4.dp))
// ===== Объединенная секция: Режим работы + Телефон =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Левая часть: Режим работы
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SectionHeader(title = "Режим работы")
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
                            if (!isOpenNow) {
                                Text(
                                    text = "Сейчас закрыто",
                                    fontSize = 13.sp,
                                    color = Orange,
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

                // Разделитель
                Spacer(modifier = Modifier.width(16.dp))

                // Правая часть: Телефон
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SectionHeader(title = "Телефон")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$telephone")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFF3B82F6)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = telephone,
                            fontSize = 15.sp,
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(4.dp))

            // ===== Секция: Услуги (Доставка и Оплата) =====
            if (navObject.hasDelivery || navObject.payment) {
                SectionHeader(title = "Услуги")

                if (navObject.hasDelivery) {
                    ServiceInfoRow(
                        icon = Icons.Default.LocalShipping,
                        title = "Доставка",
                        subtitle = "",
                        color = Color(0xFF10B981)
                    )
                }

                if (navObject.payment) {
                    ServiceInfoRow(
                        icon = Icons.Default.CreditCard,
                        title = "Оплата картой",
                        subtitle = "Принимаем банковские карты",
                        color = Color(0xFF3B82F6)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Дата публикации
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val publicationDate = dateFormat.format(navObject.timestamp)
                Text(
                    text = "Дата публикации: $publicationDate",
                    fontSize = 14.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // ===== Секция: Отзывы =====
            SectionHeader(title = "Отзывы")

            if (uiState.value.comments.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(110.dp)

                ) {
                    items(uiState.value.comments) { ratingData ->
                        CommentListItem(
                            onClick = { rData ->
                                viewModel.onEvent(
                                    DetailUiEvents.DetailUiEvent.CommentDialogEvent(true, rData)
                                )
                            },
                            ratingData = ratingData
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = TextGray.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нет отзывов. Будьте первым!",
                            color = TextGray.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            // Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
