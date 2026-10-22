package com.kodex.guide.presentation.parallaxScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PhotoGallery(photos: List<String>, height: Dp) {
    if (photos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Image,
                contentDescription = "Нет фото",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Основное фото (первое)
            AsyncImage(
                model = photos.first(),
                contentDescription = "Фото места",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )

            // Индикатор количества фото
            if (photos.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Collections,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${photos.size} фото",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Кнопка "Все фото"
            Button(
                onClick = { /* Открыть галерею */ },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Все фото", fontSize = 14.sp)
            }
        }
    }
}
