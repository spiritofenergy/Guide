package com.kodex.guide.presentation.banners

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kodex.guide.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.GreenSea
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.Premium
import kotlinx.coroutines.launch

/**
 * Слайдер баннеров (Book с isBanner = true)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSlider(
    banners: List<Book>,
    onBannerClick: (Book) -> Unit
) {
    android.util.Log.d("BannerSlider", "Баннеров получено: ${banners.size}")
    
    if (banners.isEmpty()) {
        android.util.Log.d("BannerSlider", "Баннеров нет — возвращаем пустой Column")
        return
    }

    val pagerState = rememberPagerState(
        pageCount = { banners.size }
    )
    val scope = rememberCoroutineScope()

    // Автопрокрутка баннеров влево каждые 4 секунды
    LaunchedEffect(banners.size) {
        if (banners.size > 1) {
            while (true) {
                kotlinx.coroutines.delay(4000) // Ждём 4 секунды перед сменой
                val nextPage = (pagerState.currentPage + 1) % banners.size

                // Плавная анимация перехода с замедлением в конце
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 1200, // Длительность анимации (1.2 секунды).
                        // Увеличьте до 1500-2000 для ещё более медленного скольжения.
                        easing = FastOutSlowInEasing // Быстрый старт -> плавное замедление в конце
                    )
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Слайдер
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val book = banners[page]
                BannerItem(book, onBannerClick)
            }
        }

        // Индикаторы
        if (banners.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (pagerState.currentPage == index) Orange else Color.Gray.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun BannerItem(
    book: Book,
    onBannerClick: (Book) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
            .clickable { onBannerClick(book) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Картинка
            if (book.imageUrl.isNotEmpty()) {
                val bitmap = remember(book.imageUrl) {
                    try {
                        val isBase64 = !book.imageUrl.startsWith("http")
                        if (isBase64) {
                            val decodedBytes = Base64.decode(book.imageUrl, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            if (bmp != null) bmp.asImageBitmap() else null
                        } else null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder_color),
                        error = painterResource(id = R.drawable.placeholder_color)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Без фото", color = Color.White)
                }
            }

            // 2. Текстовые поля на баннере
            val smallTextSize = 10.sp
            val largeTextSize = 14.sp
            
            // Текст слева сверху
            if (book.bannerTextTopLeft.isNotEmpty()) {
                TextWithShadow(
                    text = book.bannerTextTopLeft,
                    fontSize = largeTextSize,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }

            // Текст справа сверху
            if (book.bannerTextTopRight.isNotEmpty()) {
                TextWithShadow(
                    text = book.bannerTextTopRight,
                    fontSize = largeTextSize,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            // Текст слева снизу
            if (book.bannerTextBottomLeft.isNotEmpty()) {
                TextWithShadow(
                    text = book.bannerTextBottomLeft,
                    fontSize = smallTextSize,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }

            // Текст справа снизу
            if (book.bannerTextBottomRight.isNotEmpty()) {
                TextWithShadow(
                    text = book.bannerTextBottomRight,
                    fontSize = smallTextSize,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                )
            }

            // 3. Метка "РЕКЛАМА" в правом нижнем углу
            val adBackgroundColor = when (book.authorRole) {
                UserRole.BUSINESS.level, UserRole.PREMIUM.level -> Premium // Малиновый (Crimson)
                else -> GreenSea
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp) // Отступ от края картинки
                    .background(
                        color = adBackgroundColor.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Реклама",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Текст с тенью для отображения на баннере
 */
@Composable
fun TextWithShadow(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = Color.Black.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset(1.dp, 1.dp)
        )
        Text(
            text = text,
            fontSize = fontSize,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
