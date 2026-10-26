package com.kodex.guide.presentation.banners

import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.kodex.guide.domain.model.Banner
import com.kodex.guide.ui.theme.Orange

/**
 * Экран управления баннерами (только для админа)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBannersScreen(
    viewModel: BannerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val activeBanners by viewModel.activeBanners.collectAsStateWithLifecycle()
    var bannerToDelete by remember { mutableStateOf<Banner?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление баннерами") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (activeBanners.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Баннеров пока нет")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeBanners, key = { it.id }) { banner ->
                        BannerItemCard(
                            banner = banner,
                            onDelete = {
                                bannerToDelete = banner
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog && bannerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить баннер?") },
            text = { Text("Баннер будет удалён и не будет отображаться на главной странице") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBanner(bannerToDelete!!.id)
                        showDeleteDialog = false
                        bannerToDelete = null
                    }
                ) {
                    Text("Удалить", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun BannerItemCard(
    banner: Banner,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: просмотр баннера */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Миниатюра баннера
            BannerThumbnail(imageUrl = banner.imageUrl)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Информация о баннере
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Баннер #${banner.id.take(8)}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "postKey: ${banner.postKey}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Автор: ${banner.authorUid.take(8)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Кнопка удаления
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun BannerThumbnail(imageUrl: String) {
    val bitmap = remember(imageUrl) {
        try {
            val isBase64 = imageUrl.isNotEmpty() && !imageUrl.startsWith("http")
            if (isBase64) {
                val decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
