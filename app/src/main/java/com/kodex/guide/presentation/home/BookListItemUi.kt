package com.kodex.guide.presentation.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.R
import com.kodex.guide.ui.theme.Business
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.GreenSea
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.Red
import com.kodex.guide.ui.theme.TextGray
import com.kodex.guide.ui.theme.Premium
import kotlin.math.hypot

@SuppressLint("DefaultLocale")
@Composable
fun BookListItemUi(
    heightValue: (Int) = 0,
    titleIndex: Int,
    showEditButton: Boolean = true,
    book: Book,
    ratings: List<Int> = book.ratingsList,
    isSaved: Boolean = false,
    onEditClick: (Book) -> Unit = {},
    onDeleteClick: (Book) -> Unit = {},
    onSavedRoomClick: () -> Unit = {},
    onBookClick: (Book) -> Unit = {},
) {
    val savedViewModel: SavedPostsViewModel = hiltViewModel()
    val savedKeys by savedViewModel.savedKeys.collectAsStateWithLifecycle()

    val isSaved = savedKeys.contains(book.key)

    // ===== Определяем роль для рамки =====
    val isUser = book.authorRole == UserRole.USER.level
    val isBusiness = book.authorRole == UserRole.BUSINESS.level
    val isPremium = book.authorRole == UserRole.PREMIUM.level
    val hasAnimatedBorder = isBusiness || isPremium || isUser

    val categoryBackgroundColor = when (book.authorRole) {
        UserRole.BUSINESS.level -> GreenSea.copy(alpha = 0.5f)
        UserRole.PREMIUM.level -> Premium.copy(alpha = 0.5f)
        else -> TextGray.copy(alpha = 0.7f)
    }

    // Декодируем картинку
    val bitmap by remember(book.imageUrl) {
        mutableStateOf<Bitmap?>(runCatching {
            val base64Image = Base64.decode(book.imageUrl, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(base64Image, 0, base64Image.size)
        }.getOrNull())
    }

    // ===== АНИМАЦИЯ РАМКИ =====
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val rotationDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // ===== Градиенты для разных тарифов =====

    // BUSINESS: Оранжевый -> Белый -> Синий -> Синий -> Белый -> Оранжевый
    val userBrush = remember {
        Brush.sweepGradient(
            0.00f to ButtonColorBlue,
            0.20f to ButtonColorBlue,
            0.25f to Color.White,
            0.30f to Orange,
            0.70f to Orange,
            0.75f to Color.White,
            0.80f to ButtonColorBlue,
            1.00f to ButtonColorBlue,
        )
    }  // BUSINESS: Оранжевый -> Белый -> Синий -> Синий -> Белый -> Оранжевый
    val businessBrush = remember {
        Brush.sweepGradient(
            0.00f to ButtonColorBlue,
            0.20f to ButtonColorBlue,
            0.25f to Color.White,
            0.30f to Business,
            0.70f to Business,
            0.75f to Color.White,
            0.80f to ButtonColorBlue,
            1.00f to ButtonColorBlue,
        )
    }

    // PREMIUM: Premium -> Светлый Premium -> Белый -> Premium -> Premium
    // Лёгкий "золотой ручеёк": основной цвет Premium, вспышки белого
    // и чуть более светлого оттенка Premium для эффекта "переливания".
    val premiumBrush = remember {
        Brush.sweepGradient(
            0.00f to ButtonColorBlue,
            0.20f to ButtonColorBlue,
            0.25f to Color.White,
            0.45f to Premium,
            0.55f to Premium,
            0.65f to Color.White,
            0.70f to ButtonColorBlue,
            1.00f to ButtonColorBlue,
        )
    }

    val borderBrush = when {
        isUser -> userBrush
        isBusiness -> businessBrush
        isPremium -> premiumBrush
        else -> null
    }

    // Модификатор рамки применяем только для BUSINESS и PREMIUM.
    val borderModifier: Modifier = if (hasAnimatedBorder && borderBrush != null) {
        Modifier.drawWithContent {
            drawContent()

            val strokePx = 4.dp.toPx()
            val cornerPx = 10.dp.toPx()

            val outerPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width, size.height),
                        cornerRadius = CornerRadius(cornerPx)
                    )
                )
            }
            val innerPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            strokePx,
                            strokePx,
                            size.width - strokePx,
                            size.height - strokePx
                        ),
                        cornerRadius = CornerRadius(cornerPx - strokePx / 2f)
                    )
                )
            }
            val borderPath = Path().apply {
                op(outerPath, innerPath, PathOperation.Difference)
            }

            val maxRadius = hypot(size.width / 2f, size.height / 2f) + strokePx

            clipPath(borderPath) {
                rotate(rotationDegrees, pivot = center) {
                    drawRect(
                        brush = borderBrush,
                        topLeft = Offset(
                            center.x - maxRadius,
                            center.y - maxRadius
                        ),
                        size = Size(maxRadius * 2f, maxRadius * 2f)
                    )
                }
            }
        }
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .clickable { onBookClick(book) }
        ) {
            // ===== БЛОК С КАРТИНКОЙ И АНИМИРОВАННОЙ РАМКОЙ =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .then(borderModifier)
            ) {
                // 1. Фоновое изображение (padding только если есть рамка)
                AsyncImage(
                    model = bitmap,
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .then(
                            if (hasAnimatedBorder) Modifier.padding(4.dp) else Modifier
                        )
                        .clip(RoundedCornerShape(10.dp))
                )

                // 2. Категория
                Text(
                    " " + stringArrayResource(id = R.array.category_drawer)[book.categoryIndex] + " ",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 14.dp, top = 14.dp)
                        .background(categoryBackgroundColor, RoundedCornerShape(10.dp))
                )

                // 3. Кнопка "Закладка"
                IconButton(
                    onClick = { onSavedRoomClick() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                ) {
                    Icon(
                        if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "",
                        modifier = Modifier
                            .background(
                                Color.Gray.copy(alpha = 0.5f),
                                RoundedCornerShape(15.dp)
                            )
                            .padding(8.dp),
                        tint = if (isSaved) Orange else Color.White
                    )
                }

                // 4. Рейтинг
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ratings.isNotEmpty()) {
                        Text(
                            text = String.format("%.1f", ratings.average()),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else {
                        Text(
                            text = "0.0",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Orange
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${ratings.size})",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            // ===== НИЖНЯЯ ЧАСТЬ КАРТОЧКИ =====
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                        .weight(1F),
                    text = book.title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = book.description,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                if (!showEditButton)
                    Text(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .fillMaxWidth()
                            .weight(1F),
                        text = book.price.toString() + " p",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        fontSize = 18.sp
                    )
                Spacer(modifier = Modifier.width(5.dp))

                if (book.delivery) {
                    Icon(
                        Icons.Default.DeliveryDining,
                        contentDescription = "Delivery",
                        modifier = Modifier.size(20.dp),
                        tint = GreenSea
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                if (book.payment) {
                    Icon(
                        Icons.Default.Payment,
                        contentDescription = "Payment",
                        modifier = Modifier.size(20.dp),
                        tint = Red
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(16.dp),
                    tint = Orange
                )
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = book.village,
                    color = Color.Black,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
                )

                if (showEditButton) {
                    IconButton(onClick = { onEditClick(book) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Редактирование"
                        )
                    }
                }

                if (showEditButton) {
                    IconButton(onClick = { onDeleteClick(book) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удаление"
                        )
                    }
                }
            }
        }
    }
}