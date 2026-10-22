package com.kodex.guide.presentation.home

import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kodex.guide.R
import com.kodex.guide.domain.model.Banner
import com.kodex.guide.ui.theme.GreenSea
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.Premium
import kotlinx.coroutines.launch
import okhttp3.internal.wait

/**
 * Слайдер баннеров - ссылки на посты с фото
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSlider(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit
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
                val banner = banners[page]
                BannerItem(banner, onBannerClick)
            }
        }
    }
}
@Composable
fun BannerItem(
    banner: Banner,
    onBannerClick: (Banner) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clickable { onBannerClick(banner) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. Картинка
            if (banner.imageUrl.isNotEmpty()) {
                val bitmap = remember(banner.imageUrl) {
                    try {
                        val isBase64 = !banner.imageUrl.startsWith("http")
                        if (isBase64) {
                            val decodedBytes = Base64.decode(banner.imageUrl, Base64.DEFAULT)
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
                        contentDescription = banner.postKey,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = banner.postKey,
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

            // 2. Метка "РЕКЛАМА" в правом нижнем углу
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp) // Отступ от края картинки
                    .background(
                        color = Premium.copy(alpha = 0.7f),
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
