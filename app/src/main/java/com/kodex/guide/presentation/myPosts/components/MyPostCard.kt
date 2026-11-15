package com.kodex.guide.presentation.myPosts.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.ui.theme.ColorBlue
import com.kodex.guide.ui.theme.Orange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MyPostCard(
    book: Book,
    onEdit: () -> Unit,
    onUpload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),   // ✅ было: fillMaxW idth
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val thumbnailModifier = Modifier
                .width(72.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
            if (book.imageUrl.startsWith("http")) {
                coil.compose.AsyncImage(
                    model = book.imageUrl,
                    contentDescription = "Фото объявления: ${book.title}",
                    modifier = thumbnailModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                val imageBitmap = remember(book.imageUrl) {
                    book.imageUrl.takeIf { it.isNotBlank() }
                        ?.let(::decodeBase64ToBitmap)
                }
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = "Фото объявления: ${book.title}",
                        modifier = thumbnailModifier,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.placeholder_color),
                        contentDescription = "Фото не задано",
                        modifier = thumbnailModifier,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = book.title.ifEmpty { "Без названия" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        StatusBadge(isUploaded = book.isUploaded)
                    }
                    Spacer(Modifier.height(4.dp))

                    // Форматируем дату удаления

                    Spacer(Modifier.height(4.dp))

                    val formattedDate = remember(book.deleteDate) {
                         if (book.deleteDate > 0) {
                            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            sdf.format(Date(book.deleteDate))
                        } else {
                            "Срок не указан"
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically // Выравнивание по центру по вертикали
                    ) {
                        Text(
                            text = "Активно до:",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold, // Выделим дату жирным для акцента
                            color = Color.Gray // // Если срока нет, можно покрасить в красный или оставить серым
                        )
                    }



                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Ред.")
                        }
                        if (!book.isUploaded) {
                            Button(
                                onClick = onUpload,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Publish, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Опубл.")
                            }
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, tint = Orange, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}