package com.kodex.guide.presentation.home

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kodex.guide.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.GreenSea
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.Red
import com.kodex.guide.ui.theme.TextGray
import com.kodex.guide.ui.theme.Premium
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun BookListItemUi(
    heightValue: (Int) = 0,
    titleIndex: Int,
    showEditButton: Boolean = true,
    book: Book = Book(),
    ratings: List<Int>? = null,
    isSaved: Boolean = false,
    onEditClick: (Book) -> Unit = {},
    onDeleteClick: (Book) -> Unit = {},
    onSavedRoomClick: () -> Unit = {},
    onBookClick: (Book) -> Unit = {},
) {
    val context = LocalContext.current
    
    val categoryBackgroundColor = when (book.authorRole) {
        UserRole.BUSINESS.level -> GreenSea.copy(alpha = 0.5f)
        UserRole.PREMIUM.level -> Premium.copy(alpha = 0.7f)
        else -> TextGray.copy(alpha = 0.7f)
    }

    val effectiveRatings = ratings ?: book.ratingsList

    val imageModel = remember(book.imageUrl) {
        val isBase64 = book.imageUrl.isNotEmpty() && !book.imageUrl.startsWith("http", ignoreCase = true)
        if (isBase64) {
            try {
                val decodedBytes = Base64.decode(book.imageUrl)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        } else {
            book.imageUrl.takeIf { it.isNotEmpty() }
        }
    }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onBookClick(book)
                    }
            ) {
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
            // 1. Фоновое изображение
            AsyncImage(
                model = imageModel,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(10.dp))
            )

            // 2. Категория в левом верхнем углу
            Text(
                " ${book.categoryIndex.getLocalizedCategory(context)} ",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 10.dp)
                    .background(categoryBackgroundColor, RoundedCornerShape(10.dp))
            )

            IconButton(
                onClick = { onSavedRoomClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                Icon(
                    if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "",
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
                        .padding(8.dp),
                    tint = if (isSaved) Orange else Color.White
                )
            }

            // 5. Рейтинг в левом нижнем углу
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                 if (effectiveRatings.isNotEmpty()) {
                Text(
                    text = String.format("%.1f", effectiveRatings.average()),
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
                    text = "(${effectiveRatings.size})",
                    color = Color.White,
                    fontSize = 13.sp
                )
        }
    }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 4.dp),
                text = book.title,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }

        Text(
            text = book.description,
            color = Color.Gray,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 4.dp),
                    text = "${book.price} p",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 18.sp,
                )
                
                if(book.delivery) {
                    Icon(
                        Icons.Default.DeliveryDining,
                        contentDescription = "Delivery",
                        modifier = Modifier.size(18.dp),
                        tint = GreenSea
                    )
                }
            }
            
            Icon(
                Icons.Default.Payment,
                contentDescription = "Payment",
                modifier = Modifier.size(18.dp),
                tint = Red
            )
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(14.dp),
                tint = Orange
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
                    .defaultMinSize(minWidth = 0.dp),
                text = book.village,
                color = Color.Black,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )


            if (showEditButton) IconButton(onClick = {
                onEditClick(book)

            }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Редактирование"
                )
            }


            if (showEditButton) IconButton(onClick = {
                onDeleteClick(book)
            }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удаление"
                )
            }
        }
            }
        }
    }


/**
 * Получает локализованное название категории из string-array.
 */
fun BookCategories.getLocalizedCategory(context: android.content.Context): String {
    val categoryNames = context.resources.getStringArray(R.array.category_drawer)
    return if (this.id < categoryNames.size) {
        categoryNames[this.id]
    } else {
        this.name
    }
}
