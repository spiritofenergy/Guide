package com.kodex.guide.presentation.comments

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kodex.guide.presentation.detailScreen.CommentListItem
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.presentation.navigation.NavRoutes.CommentsNavData


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@Composable
fun CommentsScreen(
    onCommentsClick: (CommentsNavData) -> Unit = {},
    navObject: CommentsNavData = CommentsNavData(),
    viewModel: CommentsViewModel = hiltViewModel(),
    //viewModelD: DetailsScreenViewModel = hiltViewModel()
) {


    LaunchedEffect(key1 = Unit) {
         viewModel.getBookComments(navObject.bookId)

       /* viewModel.onEvent(DetailUiEvent.GetCommentsEvent(
            navObject.bookId
        ))*/
         }
    val comments = viewModel.commentsState.value
    val ratings = comments.mapNotNull { it.rating }
    // Information
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
                if (ratings.isNotEmpty()) {
                    Text(
                        text = String.format("%.1f", ratings.average()),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "(${ratings.size})",
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp
                    )
                } else {
                    Text(text = "Нет оценок")
                }

                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = Orange
                )
            }
            Spacer(Modifier.height(10.dp))

            //Comments
            if (comments.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                LazyRow(modifier = Modifier
                    .fillMaxSize()) {
                    items(comments) { ratingData ->
                            CommentListItem(
                                onClick = {


                            },
                                ratingData = ratingData
                            )
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
    }



@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun CommentsScreenPreview() {
    CommentsScreen( )
}




































/*

fun DetailScreen1(
    navObject: DetailNavObject = DetailNavObject(),
    viewModel: DetailsScreenViewModel = hiltViewModel()
) {
    val context: Context
    val text = "Опция в разработке!"
    val duration = Toast.LENGTH_SHORT


    //val context = application.
    var showReteDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var ratingDataToShow by remember { mutableStateOf(RatingData()) }

    var bitmap: Bitmap? = null
    try {
        val base64Image = Base64.decode(navObject.imageUrl, Base64.DEFAULT)
        bitmap = BitmapFactory.decodeByteArray(
            base64Image, 0,
            base64Image.size
        )
    } catch (e: IllegalArgumentException) {

    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getBookComments(navObject.bookId)
    }
    // Dialogs
    RateDialog(
        ratingData = viewModel.ratingDataState.value ?: RatingData(),
        onDismiss = {
            showReteDialog = false
        },
        onSubmit = { rating, message ->
            val ratingData = RatingData(
                name = "",
                rating = rating,
                message = message,
                lastRating = viewModel.ratingDataState.value?.rating ?: 0
            )
            viewModel.insertRating(ratingData, navObject.bookId)
            showReteDialog = false
        },
        show = showReteDialog,
    )

    CommentDialog(
        showDialog = showCommentDialog,
        onDismiss = {
            showCommentDialog = false
        },
        ratingData = ratingDataToShow,
        onConfirm = {
            showCommentDialog = false
        }
    )
   // Information
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 25.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth(0.7F)
                        .padding(top = 10.dp, bottom = 20.dp)
                        .height(190.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.FillHeight
                )
                Spacer(modifier = Modifier.width(5.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Категория:",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Text(
                        text = stringArrayResource(id = R.array.category_array)[navObject.categoryIndex],
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Автор:",
                        color = Color.Gray,
                        fontSize = 16.sp

                    )
                    if (navObject.author.isNotEmpty()) {
                        Text(
                            text = navObject.author,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else {
                        Text(
                            text = "Admin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp

                        )
                    }


                    Text(
                        text = "Дата:",
                        color = Color.Gray,
                        fontSize = 16.sp

                    )
                    Text(
                        text = navObject.timestamp.toFormattedDate().toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp

                    )
                    Text(
                        text = "Оценка",
                        color = Color.Gray,
                        fontSize = 16.sp

                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (navObject.ratingsList.isEmpty()) {
                                 "0.0"
                                }else {
                                String.format("%.1f", navObject.ratingsList.average())
                            },
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            }
                        }

                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Orange

                        )
                    }
                }
            }
    //Button
            Spacer(modifier = Modifier.width(26.dp))
            Row(modifier = Modifier.fillMaxWidth()) {

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    onClick = {
                        viewModel.getUserRating(bookId = navObject.bookId)
                        showReteDialog = true

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange
                    )
                )
                {
                    Text(text = "Оценка")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),

                    onClick = {

                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor
                    )
                )
                {
                    Text(text = "${navObject.price} Купить сейчас")


                    //Toast.makeText(context, "Опция в разрабоке")
                }
            }


            //  Spacer(modifier = Modifier.width(50.dp))
            Spacer(modifier = Modifier.fillMaxWidth().padding(10.dp))

            Text(
                text = navObject.title,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            */
/*  Text(
                text = stringArrayResource(id = R.array.category_array)[navObject.categoryIndex],
                fontWeight = FontWeight.Bold
            )*//*


            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    //.weight(1F)
            ) {
                Text(
                    text = navObject.description, fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            if (viewModel.commentState.value.isNotEmpty()) {
                Text(
                    text = "Коментарии",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(10.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.height(130.dp)
                        //.weight(0.3F)
                ) {
                    items(viewModel.commentState.value) { ratingData ->
                        CommentListItem(
                            onClick = { rData ->
                                showCommentDialog = true
                                ratingDataToShow = rData
                            },
                            ratingData = ratingData
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        )
                    }

                }
            }
                 Spacer(modifier = Modifier.width(5.dp))
        }


*/

 /*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
 fun ShowDetailScreen() {
        DetailScreen(
            navObject = DetailNavObject(
                bookId = "123",
                title = "Title",
                description = "Статья большая с кучей фотографий, а видео на целый час. Лишь терпеливый осилит! :) Всё про приключение на Тропе Голицына в Новом Свете в Крыму. Туда ехать мы вообще не планировали. Однако Бог выгодно сложил обстоятельства в нашу сторону. Таким образом, прибыли из Алушты в Новый свет 1 апреля 2024 года, словно перепрыгнув с одного курорта на другой.\n" +
                        "\n" +
                        "Мы супруги: Андрей и Елена. Приехали в Крым зимой - 19 января и рот раскрыли от удивления. Вместо ожидаемых холодных ветров и тумана, получили +15 в январе и даже искупались в море. Вместо планируемого 1 месяца, остались на три. Где помимо ежедневного наслаждения от созерцания моря в панорамном окне гостиничного номера, ходили в походы и делали вылазки в горы. Это одна из необычных вылазок, сейчас всё расскажем!",
                price = "100",
                telephone = "123456789",
                categoryIndex = 0
             )
        )
 }*/

