package com.kodex.guide.presentation.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.UserRole
import com.kodex.bookmarketcompose.R
import com.kodex.guide.ui.theme.BoxFilter
import com.kodex.guide.ui.theme.GreenSea
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.Red
import com.kodex.guide.ui.theme.TextGray
import com.kodex.guide.ui.theme.AccentPurple
import com.kodex.guide.ui.theme.Premium


@SuppressLint("DefaultLocale")
@Composable
fun BookListItemUi(
    heightValue: (Int) = 0,
    titleIndex: Int,
    showEditButton: Boolean = true,
    book: Book = Book(),
    ratings: List<Int> = book.ratingsList,
    isSaved: Boolean = false,
    onEditClick: (Book) -> Unit = {},
    onDeleteClick: (Book) -> Unit = {},
    onSavedRoomClick: () -> Unit = {},
    onBookClick: (Book) -> Unit = {},
) {
    val categoryBackgroundColor = when (book.authorRole) {
        UserRole.BUSINESS.level -> GreenSea.copy(alpha = 0.5f)
        UserRole.PREMIUM.level -> Premium.copy(alpha = 0.5f)
        else -> TextGray.copy(alpha = 0.7f)
    }

    var bitmap: Bitmap? = null
    try {
        val base64Image = Base64.decode(book.imageUrl, Base64.DEFAULT)
        bitmap = BitmapFactory.decodeByteArray(
            base64Image, 0,
            base64Image.size
        )
    } catch (e: IllegalArgumentException){

    }

        Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
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
               // model = book.imageUrl.toBitmap(),
                model =  bitmap,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(10.dp))
            )

            // 2. Категория в левом верхнем углу
            Text(
                " " + stringArrayResource(id = R.array.category_drawer)[book.categoryIndex.id]+ " ",
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

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
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
            fontSize = 16.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.height(5.dp))
            if (!showEditButton)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    text = book.price.toString() + " p",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 18.sp
                )
            Spacer(modifier = Modifier.width(5.dp))

            if(book.delivery) {
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
                    modifier = Modifier
                        .padding(10.dp),
                    text = book.village,
                    color = Color.Black,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
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
